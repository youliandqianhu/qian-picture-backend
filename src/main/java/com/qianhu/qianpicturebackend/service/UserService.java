package com.qianhu.qianpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qianhu.qianpicturebackend.model.dto.user.UserQueryRequest;
import com.qianhu.qianpicturebackend.model.entity.User;
import com.qianhu.qianpicturebackend.model.vo.LoginUserVO;
import com.qianhu.qianpicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author hxz
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2026-03-14 20:13:39
*/
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 获取脱敏用户信息(管理员用)
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏用户信息列表(管理员用)
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 接收查询条件
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     *  密码加密
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);
}
