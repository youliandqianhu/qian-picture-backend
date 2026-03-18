package com.qianhu.qianpicturebackend.api.imagesearch;

import com.qianhu.qianpicturebackend.api.imagesearch.model.ImageSearchResult;
import com.qianhu.qianpicturebackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.qianhu.qianpicturebackend.api.imagesearch.sub.GetImageListApi;
import com.qianhu.qianpicturebackend.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {

    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(String[] args) {
        // 测试以图搜图功能(百度搜索现在的这个Acs-Token有时会失效。。需要时常更新GetImagePageUrlApi中的Acs-Token值，用火狐浏览器搜索能得到正常的)
        String imageUrl = "https://cdn.pixabay.com/photo/2020/04/13/19/40/sun-5039871_1280.jpg";
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        System.out.println("结果列表" + resultList);
    }
}
