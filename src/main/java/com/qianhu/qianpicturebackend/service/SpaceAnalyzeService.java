package com.qianhu.qianpicturebackend.service;

import com.qianhu.qianpicturebackend.model.dto.space.analyze.*;
import com.qianhu.qianpicturebackend.model.entity.Space;
import com.qianhu.qianpicturebackend.model.entity.User;
import com.qianhu.qianpicturebackend.model.vo.space.analyze.*;

import java.util.List;

public interface SpaceAnalyzeService {

    /**
     * 获取空间分析数据
     * @param spaceUsageAnalyzeRequest
     * @param loginUser
     * @return
     */
    public SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    /**
     * 获取空间用户分析数据
     * @param spaceUserAnalyzeRequest
     * @param loginUser
     * @return
     */
    public List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    /**
     * 获取空间图片大小分析数据
     * @param spaceSizeAnalyzeRequest
     * @param loginUser
     * @return
     */
    public List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    /**
     * 获取空间标签分析数据
     * @param spaceTagAnalyzeRequest
     * @param loginUser
     * @return
     */
    public List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    /**
     * 获取空间排名分析数据
     * @param spaceRankAnalyzeRequest
     * @param loginUser
     * @return
     */
    public List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);

    /**
     * 获取空间分类分析数据
     * @param spaceCategoryAnalyzeRequest
     * @param loginUser
     * @return
     */
    public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);
}
