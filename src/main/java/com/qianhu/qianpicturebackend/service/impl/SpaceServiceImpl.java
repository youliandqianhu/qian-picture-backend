package com.qianhu.qianpicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianhu.qianpicturebackend.exception.BusinessException;
import com.qianhu.qianpicturebackend.exception.ErrorCode;
import com.qianhu.qianpicturebackend.exception.ThrowUtils;
import com.qianhu.qianpicturebackend.manager.sharding.DynamicShardingManager;
import com.qianhu.qianpicturebackend.mapper.SpaceMapper;
import com.qianhu.qianpicturebackend.model.dto.space.SpaceAddRequest;
import com.qianhu.qianpicturebackend.model.dto.space.SpaceQueryRequest;
import com.qianhu.qianpicturebackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.qianhu.qianpicturebackend.model.entity.Space;
import com.qianhu.qianpicturebackend.model.entity.SpaceUser;
import com.qianhu.qianpicturebackend.model.entity.User;
import com.qianhu.qianpicturebackend.model.enums.SpaceLevelEnum;
import com.qianhu.qianpicturebackend.model.enums.SpaceRoleEnum;
import com.qianhu.qianpicturebackend.model.enums.SpaceTypeEnum;
import com.qianhu.qianpicturebackend.model.vo.SpaceVO;
import com.qianhu.qianpicturebackend.model.vo.UserVO;
import com.qianhu.qianpicturebackend.service.SpaceService;
import com.qianhu.qianpicturebackend.service.SpaceUserService;
import com.qianhu.qianpicturebackend.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author hxz
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2026-03-17 18:29:05
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService {

    @Resource
    private UserService userService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private SpaceUserService spaceUserService;

//    TODO 暂时放弃分库分表。
//    @Resource
//    @Lazy
//    private DynamicShardingManager dynamicShardingManager;


    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 在此次实体类和DTO进行转换
        Space space = new Space();
        BeanUtil.copyProperties(spaceAddRequest, space);
        // 设置默认值
        if (StrUtil.isBlank(space.getSpaceName())){
            space.setSpaceName("默认空间");
        }
        if (space.getSpaceLevel() == null){
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        // 新增空间类型默认值
        if (space.getSpaceType() == null){
            space.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        // 填充数据
        fillSpaceBySpaceLevel(space);
        // 数据校验
        validSpace(space, true);
        Long userId = loginUser.getId();
        space.setUserId(userId);
        // 权限校验
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        // 针对用户进行加锁
        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            Long newSpaceId = transactionTemplate.execute(status -> {
                if (!userService.isAdmin(loginUser)){
                    boolean exists = this.lambdaQuery()
                            .eq(Space::getUserId, userId)
                            .eq(Space::getSpaceType, spaceAddRequest.getSpaceType())
                            .exists();
                    ThrowUtils.throwIf(exists, ErrorCode.PARAMS_ERROR, "每个用户每类空间仅能创建一个");
                }
                // 管理员可以创建多个
                // 写入数据库
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

                // 判断是否新增的是团队空间
                if (SpaceTypeEnum.TEAM.getValue() == spaceAddRequest.getSpaceType()) {
                    // 成员空间自动添加该角色为管理员
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                    spaceUser.setUserId(loginUser.getId());
                    spaceUser.setSpaceId(space.getId());
                    result = spaceUserService.save(spaceUser);
                    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"创建团队成员记录失败");
                }
                // 创建分表
                // TODO 暂时放弃分库分表。
                // TODO 实际上存在很多问题，比如我原先的公共图库spaceId都为null,加了分库分表后spaceId是不能为null的,也就是我空间的增删改查都要求spaceId有个默认值(0就行)，然后之后的判断也要判断spaceId为0，比较麻烦
                // dynamicShardingManager.createSpacePictureTable(space);
                // 返回新写入的数据id
                return space.getId();
            });
            // 返回结果是包装类,可以做一些处理
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }
    }

    /**
     * 校验空间信息
     * @param space
     * @param add
     */
    @Override
    public void validSpace(Space space, boolean add){
        // 校验参数
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        Integer spaceType = space.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(spaceType);
        // 判断是否创建
        if (add) {
            if (StrUtil.isBlank(spaceName)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间等级不能为空");
            }
            // 新增空间类型判断
            if (spaceType == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类型不能为空");
            }
        }
        // 修改数据时，如果要改空间级别
        if (spaceLevel != null && spaceLevelEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间等级不存在");
        }
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
        // 新增空间类型判断
        if (spaceType != null && spaceTypeEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类型不存在");
        }
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // 对象列表 => 封装对象列表
        List<SpaceVO> spaceVOList = spaceList.stream()
                .map(SpaceVO::objToVo)
                .collect(Collectors.toList());
        // 1. 关联查询用户信息
        // 1,2,3,4
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        // 1 => user1, 2 => user2
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        Integer spaceType = spaceQueryRequest.getSpaceType();
        // 拼接查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "spaceType", spaceType);
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 根据空间级别,自动填充限额
     * @param space
     */
    @Override
    public void fillSpaceBySpaceLevel(Space space){
        // 根据空间级别,自动填充限额
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null){
            // 如果space本身没有设置最大图片总大小和最大图片总数量才根据空间级别设置
            if (space.getMaxSize() == null){
                space.setMaxSize(spaceLevelEnum.getMaxSize());
            }
            if (space.getMaxCount() == null){
                space.setMaxCount(spaceLevelEnum.getMaxCount());
            }

        }
    }

    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        // 仅本人或管理员可编辑
        if (!space.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }
}




