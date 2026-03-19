package com.qianhu.qianpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qianhu.qianpicturebackend.model.dto.space.SpaceAddRequest;
import com.qianhu.qianpicturebackend.model.dto.space.SpaceQueryRequest;
import com.qianhu.qianpicturebackend.model.entity.Space;
import com.qianhu.qianpicturebackend.model.entity.User;
import com.qianhu.qianpicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;


/**
* @author hxz
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2026-03-17 18:29:05
*/
public interface SpaceService extends IService<Space> {

    /**
     * 添加空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 校验空间信息
     * @param space
     * @param add
     */
    public void validSpace(Space space, boolean add);

    /**
     * 获取空间封装类
     * @param space
     * @param request
     * @return
     */
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间封装类列表
     * @param spacePage
     * @param request
     * @return
     */
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取空间查询构造器
     * @param spaceQueryRequest
     * @return
     */
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);



    /**
     * 设置默认空间属性
     * @param space
     */
    public void fillSpaceBySpaceLevel(Space space);

    /**
     * 空间权限校验
     * @param loginUser
     * @param space
     */
    public void checkSpaceAuth(User loginUser, Space space);
}
