package com.qianhu.qianpicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureTagCategory implements Serializable {

    /**
     * 标签类列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */
    private List<String> categoryList;

    private static final Long serialVersionUID = 1L;
}
