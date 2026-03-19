package com.qianhu.qianpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qianhu.qianpicturebackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.qianhu.qianpicturebackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.qianhu.qianpicturebackend.model.entity.SpaceUser;
import com.qianhu.qianpicturebackend.model.vo.spaceuser.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author hxz
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2026-03-19 15:47:03
*/
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 添加团队空间成员
     * @param spaceUserAddRequest
     * @return
     */
    public long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验团队空间
     * @param spaceUser
     * @param add
     */
    public void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取查询条件
     * @param spaceUserQueryRequest
     * @return
     */
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 获取空间用户VO
     * @param spaceUser
     * @param request
     * @return
     */
    public SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间用户VO列表
     * @param spaceUserList
     * @return
     */
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
