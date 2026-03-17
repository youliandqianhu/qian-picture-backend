package com.qianhu.qianpicturebackend.manager;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.qianhu.qianpicturebackend.config.CosClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传文件
     * @param key
     * @param localFile
     * @return
     */
    public PutObjectResult putObject(String key, File localFile) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, localFile);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传图片(使用万象存储)
     * @param key
     * @param localFile
     * @return
     */
    public PutObjectResult putPictureObject(String key, File localFile){
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, localFile);
        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1);
        // 添加图片处理规则
        List<PicOperations.Rule> ruleList = new LinkedList<>();
        // 图片压缩(转换成.webp格式)
        String webpKey = FileUtil.mainName(key) + ".webp";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(webpKey);
        compressRule.setRule("imageMogr2/format/webp");
        ruleList.add(compressRule);
        // 图片缩略图(大于20KB才进行缩略图,不然可能比压缩图还大)
        if (localFile.length() > 20 * 1024){
            PicOperations.Rule thumbnail = new PicOperations.Rule();
            thumbnail.setBucket(cosClientConfig.getBucket());
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            thumbnail.setFileId(thumbnailKey);
            // 缩放规则 /thumbnail/<Width>x<Height>>（如果大于原图宽高，则不处理）
            // 具体请看:https://cloud.tencent.com/document/product/436/113295
            thumbnail.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 128, 128));
            ruleList.add(thumbnail);
        }
        // 构造处理参数
        picOperations.setRules(ruleList);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载文件
     * @param key
     * @return
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 删除对象
     *
     * @param url 文件 url
     */
    public void deleteObject(String url) throws CosClientException {
        // 自动处理数据库中图片包含域名的情况
        String host = cosClientConfig.getHost();
        String key = url.replace(host, "");
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }

}
