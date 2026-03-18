package com.qianhu.qianpicturebackend.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import com.qianhu.qianpicturebackend.config.CosClientConfig;
import com.qianhu.qianpicturebackend.exception.BusinessException;
import com.qianhu.qianpicturebackend.exception.ErrorCode;
import com.qianhu.qianpicturebackend.exception.ThrowUtils;
import com.qianhu.qianpicturebackend.manager.CosManager;
import com.qianhu.qianpicturebackend.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param inputSource    文件输入源
     * @param uploadPathPrefix 上传路径前缀
     * @return
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 校验文件
        validPicture(inputSource);
        // 获取上传路径
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginalFilename(inputSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFilename);
        // 上传文件
        File file = null;
        try{
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            // 处理文件来源
            processFile(inputSource, file);
            // 上传图片
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 由于CosManager里面我们做了图像处理,将图片转换为webp格式
            // 图片上传完成后，对象存储（Cloud Object Storage，COS）会存储原始图片(ImageInfo)和已处理过的图片(ProcessResults)
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 获取处理后图片
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                // TODO 如果处理成功就把原图删除，后续没有能删原图的机会了
                // 删除原图
                String key = putObjectResult.getCiUploadResult().getOriginalInfo().getKey();
                cosManager.deleteObject(key);
                // 获取转换后的图webp格式的
                CIObject compressedCiObject = objectList.get(0);
                // 缩略图默认等于压缩图
                CIObject thumbnailCiObject = compressedCiObject;
                // 有生成缩略图,才拿缩略图
                if (objectList.size() > 1){
                    thumbnailCiObject = objectList.get(1);
                }
                // 封装压缩图返回结果
                return buildResult(originalFilename,compressedCiObject,thumbnailCiObject, imageInfo);
            }
            // 封装原图片返回结果
            return buildResult(imageInfo, uploadPath,originalFilename,file);
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * 获取图片名称
     * @param inputSource
     */
    public abstract String getOriginalFilename(Object inputSource);

    /**
     * 下载文件
     * @param inputSource
     * @param file
     */
    public abstract void processFile(Object inputSource, File file) throws IOException;

    /**
     * 校验文件
     *
     * @param inputSource 输入源
     */
    public abstract void validPicture(Object inputSource);

    /**
     * 封装原始图片返回结果
     * @param imageInfo
     * @param uploadFilePath
     * @return
     */
    private UploadPictureResult buildResult(ImageInfo imageInfo, String uploadFilePath, String originalFilename,File file){
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadFilePath);
        uploadPictureResult.setPicColor(imageInfo.getAve());
        return uploadPictureResult;
    }

    /**
     * 封装压缩图片返回结果
     * @param originalFilename
     * @param compressedCiObject
     * @return
     */
    private UploadPictureResult buildResult(String originalFilename, CIObject compressedCiObject, CIObject thumbnailCiObject, ImageInfo imageInfo){
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat());
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue());
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        uploadPictureResult.setPicColor(imageInfo.getAve());
        return uploadPictureResult;
    }

    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

}
