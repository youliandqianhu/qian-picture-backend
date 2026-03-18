package com.qianhu.qianpicturebackend.api.imagesearch.sub;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.qianhu.qianpicturebackend.exception.BusinessException;
import com.qianhu.qianpicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GetImagePageUrlApi {

    /**
     * 获取图片页面地址
     *
     * @param imageUrl
     * @return
     */
    public static String getImagePageUrl(String imageUrl) {
        // 1. 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        // 这个值时常会失效。。。
        String acsToken = "1773811110469_1773814816654_vBCXRK6hqAEW1lxmMv7Sn5TFtKxwcqsjfeSs9Ng7fi/ygjZkr7y88VKVXdkbltKXMTdla0XPQjlk9/+tMYcPBukw1sJhEKpo+wXMmtD33dpbwj7VyVzHTJLRuSB3f1KctyZF5n7OME9es8F28Jq4MbABlpBP1OI3ANy0W4kx2OQOfGnvBVVER4wEBT3UyarCy1mgazkkgkOImLdN7J7II9RnWjRngO3aejDCaT5YnzTnEct11V205VTsShXegRBVWc9FP5Tu9Vjjt244orYTcY4hVdjCJlmreQ791qYudtOry6PApEfNLSKG2yM26hRIUttQTmtMnNcI/JgJHD4DnwxynEv7CTdITYIrBQzcs4+wuOiKDtORJSPZevH6+kHUtWUu8GoHEef50i0ePfftl8AP+rpxpglxyaezCcz1tzoGYqatj8XO74xSIWhiU67k";
        try {
            // 2. 发送 POST 请求到百度接口
            HttpResponse response = HttpRequest.post(url)
                    .header("Acs-Token", acsToken)
                    .form(formData)
                    .timeout(5000)
                    .execute();
            // 判断响应状态
            if (HttpStatus.HTTP_OK != response.getStatus()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            // 解析响应
            String responseBody = response.body();
            Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);

            // 3. 处理响应结果
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            String rawUrl = (String) data.get("url");
            // 对 URL 进行解码
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // 如果 URL 为空
            if (searchResultUrl == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效结果");
            }
            return searchResultUrl;
        } catch (Exception e) {
            log.error("搜索失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }


    public static void main(String[] args) {
        // 测试以图搜图功能
//        String imageUrl = "https://www.codefather.cn/logo.png";
        String imageUrl = "https://cdn.pixabay.com/photo/2020/04/13/19/40/sun-5039871_1280.jpg";
        String result = getImagePageUrl(imageUrl);
        System.out.println("搜索成功，结果 URL：" + result);
    }
}
