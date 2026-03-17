package com.qianhu.qianpicturebackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.qianhu.qianpicturebackend.exception.BusinessException;
import com.qianhu.qianpicturebackend.exception.ErrorCode;
import com.qianhu.qianpicturebackend.exception.ThrowUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.http.Method.HEAD;

@Service
public class UrlPictureUpload extends PictureUploadTemplate{

    private static final List<String> ALLOW_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "webp"
    );

    @Override
    public String getOriginalFilename(Object inputSource) {
        String fileUrl = (String) inputSource;

        // 1. 优先从 URL 路径提取
        String fromPath = FileNameUtil.getName(fileUrl);
        if (hasValidExtension(fromPath)) {
            return fromPath;
        }

        // 2. 从响应头获取
        return getFileNameFromHeaders(fileUrl);
    }

    @Override
    public void processFile(Object inputSource, File file) throws IOException {
        String fileUrl = (String) inputSource;
        HttpUtil.downloadFile(fileUrl, file);
    }

    @Override
    public void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
        // 校验地址
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址不能为空");
        // 校验URL
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件地址格式不正确");
        }
        // 校验Url协议
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")),
                ErrorCode.PARAMS_ERROR, "仅支持HTTP 或 HTTPS 协议的文件地址");
        // 获取HEAD信息
        HttpResponse response = null;
        try{
            response = HttpUtil.createRequest(HEAD, fileUrl).execute();
            if (response.getStatus() != HttpStatus.HTTP_OK){
                // 如果没有值则不做处理
                return;
            }
            // 获取文件类型
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)){
                // 允许的图片类型
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()), ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
            // 获取文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)){
                try{
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long THREE_MB = 3 * 1024 * 1024;
                    ThrowUtils.throwIf(contentLength > THREE_MB, ErrorCode.PARAMS_ERROR, "文件大小不能超过3MB");
                }catch (NumberFormatException e){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小错误");
                }
            }
        } finally {
            if (response != null){
                response.close();
            }
        }
    }

    private boolean hasValidExtension(String filename) {
        if (StrUtil.isBlank(filename)) return false;
        String ext = FileUtil.getSuffix(filename);
        return StrUtil.isNotBlank(ext) &&
                ALLOW_EXTENSIONS.contains(ext.toLowerCase());
    }

    private String getFileNameFromHeaders(String fileUrl) {
        HttpResponse response = null;
        try {
            response = HttpUtil.createRequest(HEAD, fileUrl).execute();

            // 1. 尝试从 Content-Disposition 获取
            String disposition = response.header("Content-Disposition");
            if (StrUtil.isNotBlank(disposition)) {
                String fromDisposition = parseFilenameFromDisposition(disposition);
                if (StrUtil.isNotBlank(fromDisposition)) {
                    return fromDisposition;
                }
            }

            // 2. 用 Content-Type 生成默认名
            String contentType = response.header("Content-Type");
            return generateDefaultFileName(contentType);

        } catch (Exception e) {
            // 兜底：时间戳 + jpg
            return "image_" + System.currentTimeMillis() + ".jpg";
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private String parseFilenameFromDisposition(String disposition) {
        Pattern pattern = Pattern.compile("filename[^;=\\n]*=((['\"]).*?\\2|[^;\\n]*)");
        Matcher m = pattern.matcher(disposition);
        if (m.find()) {
            String filename = m.group(1);
            return filename.replaceAll("['\"]", "");
        }
        return null;
    }

    private String generateDefaultFileName(String contentType) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        if (StrUtil.isBlank(contentType)) {
            return "image_" + timestamp + ".jpg";
        }

        switch (contentType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return "image_" + timestamp + ".jpg";
            case "image/png":
                return "image_" + timestamp + ".png";
            case "image/webp":
                return "image_" + timestamp + ".webp";
            default:
                return "image_" + timestamp + ".jpg";
        }
    }
}
