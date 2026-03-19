package com.qianhu.qianpicturebackend.manager.auth;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.qianhu.qianpicturebackend.manager.auth.model.SpaceUserAuthConfig;
import com.qianhu.qianpicturebackend.manager.auth.model.SpaceUserRole;
import com.qianhu.qianpicturebackend.model.entity.Space;
import com.qianhu.qianpicturebackend.model.entity.SpaceUser;
import com.qianhu.qianpicturebackend.model.entity.User;
import com.qianhu.qianpicturebackend.model.enums.SpaceRoleEnum;
import com.qianhu.qianpicturebackend.model.enums.SpaceTypeEnum;
import com.qianhu.qianpicturebackend.service.SpaceUserService;
import com.qianhu.qianpicturebackend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SpaceUserAuthManager {

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private UserService userService;

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据角色获取权限列表
     */
    public List<String> getPermissionsByRole(String spaceUserRole) {
        if (StrUtil.isBlank(spaceUserRole)) {
            return new ArrayList<>();
        }
        // 找到匹配的角色
        SpaceUserRole role = SPACE_USER_AUTH_CONFIG.getRoles().stream()
                .filter(r -> spaceUserRole.equals(r.getKey()))
                .findFirst()
                .orElse(null);
        if (role == null) {
            return new ArrayList<>();
        }
        return role.getPermissions();
    }

    public List<String> getPermissionList(Space space, User loginUser){
        if (loginUser == null){
            return new ArrayList<>();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSIONS = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 公共图库
        if(space == null){
            if (userService.isAdmin(loginUser)){
                return ADMIN_PERMISSIONS;
            }
            return new ArrayList<>();
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null){
            return new ArrayList<>();
        }
        // 根据空间获取对应的权限
        switch (spaceTypeEnum){
            case PRIVATE:
                // 私有空间
                if (space.getUserId().equals(loginUser.getId()) || userService.isAdmin(loginUser)){
                    return ADMIN_PERMISSIONS;
                }else {
                    return new ArrayList<>();
                }
            case TEAM:
                // 团队空间
                SpaceUser spaceUser = spaceUserService.lambdaQuery()
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .one();
                if (spaceUser == null){
                    return new ArrayList<>();
                }else {
                    return getPermissionsByRole(spaceUser.getSpaceRole());
                }
        }
        return new ArrayList<>();
    }

}
