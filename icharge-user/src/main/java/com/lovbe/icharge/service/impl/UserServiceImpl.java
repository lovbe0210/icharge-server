package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.db.Page;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.EncorageBehaviorEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.common.config.ServiceProperties;
import com.lovbe.icharge.dao.EncourageLogMapper;
import com.lovbe.icharge.entity.dto.DomainContentUpdateDTO;
import com.lovbe.icharge.entity.dto.UpdateUserDTO;
import com.lovbe.icharge.dao.UserMapper;
import com.lovbe.icharge.entity.vo.EncourageLogVo;
import com.lovbe.icharge.entity.vo.UserStatisticVo;
import com.lovbe.icharge.service.AccountService;
import com.lovbe.icharge.service.UserService;
import com.lovbe.icharge.service.feign.StorageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: lovbe
 * @Date: 2024/8/13 22:38
 * @Description: MS
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private AccountService accountService;
    @Resource
    private StorageService storageService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommonService commonService;
    @Resource
    private ServiceProperties properties;
    @Resource
    private EncourageLogMapper encourageLogMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginUser createUserIfAbsent(AuthUserDTO authUserDTO) {
        AccountDo account = accountService.getAccountInfo(authUserDTO);
        if (account != null) {
            // 记录登录日志
            account.setLastLoginIp(authUserDTO.getUserIp())
                    .setLastLoginTime(new Date())
                    .setLoginAgent(authUserDTO.getLoginAgent())
                    .setLoginOs(authUserDTO.getLoginOs())
                    .setLoginCount(account.getLoginCount() + 1)
                    .setUpdateTime(new Date());
            accountService.updateAccount(account);
            LoginUser loginUser = new LoginUser(account.getMobile(), account.getEmail(), account.getPassword());
            loginUser.setStatus(account.getStatus());
            loginUser.setUid(account.getUid());
            return loginUser;
        }
        long uid = YitIdHelper.nextId();
        // 创建账号
        account = new AccountDo()
                .setMobile(authUserDTO.getMobile())
                .setEmail(authUserDTO.getEmail())
                .setLoginCount(1)
                .setLastLoginIp(authUserDTO.getUserIp())
                .setLastLoginTime(new Date());
        account.setUid(uid);
        account.setCreateTime(new Date());
        account.setUpdateTime(new Date());
        accountService.createAccount(account);
        // 创建用户
        UserInfoDo userInfoDo = new UserInfoDo()
                .setUsername("用户" + IdUtil.nanoId(4))
                .setDomain(createDomain(uid));
        userInfoDo.setUid(uid);
        userInfoDo.setCreateTime(new Date());
        userInfoDo.setUpdateTime(new Date());
        userMapper.insert(userInfoDo);
        LoginUser loginUser = new LoginUser(account.getMobile(), account.getEmail(), account.getPassword());
        loginUser.setStatus(CommonStatusEnum.NORMAL.getStatus());
        loginUser.setUid(uid);
        return loginUser;
    }

    @Override
    public LoginUser getLoginUserByPayload(AuthUserDTO authUserDTO) {
        AccountDo account = accountService.getAccountInfo(authUserDTO);
        if (account != null) {
            // 记录登录日志
            account.setLastLoginIp(authUserDTO.getUserIp())
                    .setLastLoginTime(new Date())
                    .setLoginAgent(authUserDTO.getLoginAgent())
                    .setLoginOs(authUserDTO.getLoginOs())
                    .setLoginCount(account.getLoginCount() + 1)
                    .setUpdateTime(new Date());
            accountService.updateAccount(account);
            LoginUser loginUser = new LoginUser(account.getMobile(), account.getEmail(), account.getPassword());
            loginUser.setStatus(account.getStatus());
            loginUser.setUid(account.getUid());
            return loginUser;
        }
        return null;
    }

    @Override
    public UserInfoDo getUserInfo(Long userId) {
        UserInfoDo userInfoDo = commonService.getCacheUser(userId);
        if (CommonStatusEnum.DISABLE.getStatus().equals(userInfoDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.USER_DISABLED);
        }
        if (CommonStatusEnum.DELETE.getStatus().equals(userInfoDo.getStatus()) || userInfoDo.getUid() == null) {
            throw new ServiceException(ServiceErrorCodes.USER_NOT_EXIST);
        }
        // 完成今日登录
        String dailyEncourageKey = RedisKeyConstant.getUserdailyEncourage(userId, SysConstant.LEVEL_ENCOURAGE_LOGIN);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        boolean absent = RedisUtil.setnx(dailyEncourageKey);
        if (absent) {
            // 今日首次登录，经验值+5
            RedisUtil.setExpireAt(dailyEncourageKey, c.getTime());
            commonService.updateUserLevel(userId, 5);
        }
        return userInfoDo;
    }

    @Override
    public UserInfoDo getUserInfo(String domain) {
        UserInfoDo userInfoDo = userMapper.selectOne(new LambdaQueryWrapper<UserInfoDo>()
                .eq(UserInfoDo::getDomain, domain));
        if (userInfoDo == null) {
            throw new ServiceException(ServiceErrorCodes.USER_NOT_EXIST);
        }
        if (!CommonStatusEnum.NORMAL.getStatus().equals(userInfoDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.USER_DISABLED);
        }
        return userInfoDo;
    }

    @Override
    public void updateUserInfo(Long userId, UpdateUserDTO userDTO) {
        // 频率限制
        CommonUtils.checkUploadFrequencyLimit(String.valueOf(userId),
                SysConstant.FILE_SCENE_AVATAR, properties.getUploadLimit());
        // 业务参数校验
        UserInfoDo userInfo = new UserInfoDo();
        userInfo.setUpdateTime(new Date());
        userInfo.setUid(userId);
        String tags = userDTO.getTagArray();
        if (StringUtils.hasLength(tags)) {
            userInfo.setTags(JSONUtil.toList(tags, Map.class));
        }
        BeanUtil.copyProperties(userDTO, userInfo);
        MultipartFile avatarFile = userDTO.getAvatarFile();
        if (avatarFile != null) {
            ResponseBean<String> upload = storageService.upload(new FileUploadDTO(avatarFile, SysConstant.FILE_SCENE_AVATAR, String.valueOf(userId)));
            if (!upload.isResult()) {
                log.error("[更新用户信息] --- 头像上传失败，errorInfo: {}", upload.getMessage());
                throw new ServiceException(ServiceErrorCodes.USER_INFO_UPDATE_FAILED);
            }
            userInfo.setAvatarUrl(upload.getData());
        }
        userMapper.updateById(userInfo);
        String cacheUserKey = RedisKeyConstant.getCacheUserKey(userId);
        RedisUtil.del(cacheUserKey);
        // 更新elasticsearch数据
        UserEsEntity userEsEntity = new UserEsEntity()
                .setUid(userId)
                .setUsername(userDTO.getUsername());
        commonService.updateElasticsearchUser(Arrays.asList(userEsEntity));
    }

    @Override
    public PageBean<PageBean<UserInfoDo>> getUserInfoList(Page page) {
        page = page == null ? new Page() : page;
        List<UserInfoDo> userInfoList = userMapper.selectList(new LambdaQueryWrapper<UserInfoDo>()
                .eq(UserInfoDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .orderByDesc(UserInfoDo::getCreateTime)
                .last(" limit " + page.getStartIndex() + "," + page.getPageSize()));
        List<UserInfoDo> list = CollectionUtils.isEmpty(userInfoList) ? List.of() : userInfoList;
        return new PageBean(list.size() == page.getPageSize(), list);
    }

    @Override
    public AuthRequest getAuthRequest() {
        return new AuthQqRequest(AuthConfig.builder()
                .clientId(properties.getQqAppId())
                .clientSecret(properties.getQqAppKey())
                .redirectUri(properties.getQqRedirectUrl())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDomainContent(DomainContentUpdateDTO contentUpdateDTO, Long userId) {
        // 获取个人信息
        UserInfoDo userInfo = commonService.getCacheUser(userId);
        if (!CommonStatusEnum.isNormal(userInfo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.AUTH_USER_STATUS_ERROR);
        }
        if (contentUpdateDTO.getContentId() == null) {
            contentUpdateDTO.setContentId(YitIdHelper.nextId());
            userMapper.update(new UpdateWrapper<UserInfoDo>()
                    .eq("uid", userId)
                    .set("content_id", contentUpdateDTO.getContentId()));
        }
        contentUpdateDTO.setContent(JsonUtils.toJsonString(contentUpdateDTO.getContent()));
        userMapper.updateDomainContent(contentUpdateDTO);
        String cacheUserKey = RedisKeyConstant.getCacheUserKey(userId);
        RedisUtil.del(cacheUserKey);
    }

    @Override
    public Object getDomainContent(Long contentId) {
        Object content = userMapper.getDomainContent(contentId);
        return content;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDomainContent(DomainContentUpdateDTO data, Long userId) {
        userMapper.deleteDomainContent(data.getContentId());
        userMapper.update(new UpdateWrapper<UserInfoDo>()
                .eq("uid", userId)
                .set("content_id", null));
        String cacheUserKey = RedisKeyConstant.getCacheUserKey(userId);
        RedisUtil.del(cacheUserKey);
    }

    @Override
    public UserStatisticVo getStatisticInfo(Long userId) {
        TargetStatisticDo statisticDo = userMapper.selectStatisticInfo(userId);
        UserStatisticVo statisticVo = new UserStatisticVo();
        BeanUtil.copyProperties(statisticDo, statisticVo);
        return statisticVo;
    }

    @Override
    public Map<String, Integer> getLevelExp() {
        return Map.of("level1", properties.getLevel1Exp(),
                "level2", properties.getLevel2Exp(),
                "level3", properties.getLevel3Exp(),
                "level4", properties.getLevel4Exp(),
                "level5", properties.getLevel5Exp(),
                "level6", properties.getLevel6Exp());
    }

    @Override
    public Object getEncourageDaily(Long userId) {
        String loginKey = RedisKeyConstant.getUserdailyEncourage(userId, SysConstant.LEVEL_ENCOURAGE_LOGIN);
        boolean hasLogin = RedisUtil.hasKey(loginKey);
        String readKey = RedisKeyConstant.getUserdailyEncourage(userId, SysConstant.LEVEL_ENCOURAGE_READ);
        List<Object> readList = RedisUtil.hgetAll(readKey);
        String writeKey = RedisKeyConstant.getUserdailyEncourage(userId, SysConstant.LEVEL_ENCOURAGE_WRITE);
        boolean hasWrite = RedisUtil.hasKey(writeKey);
        return Map.of("hasLogin", hasLogin,
                "loginExp", 5,
                "readSize", readList == null ? 0 : readList.size() > 5 ? 5 : readList.size(),
                "readExp", 2,
                "maxRead", 5,
                "hasWrite", hasWrite,
                "writeExp", 5);
    }

    @Override
    public Object getEncourageRule() {
        return List.of(Map.of("behaviorType", EncorageBehaviorEnum.BEHAVIOR_PUBLISH.getDesc(), "encourageScore", EncorageBehaviorEnum.BEHAVIOR_PUBLISH.getEncourageScore()),
                Map.of("behaviorType", EncorageBehaviorEnum.BEHAVIOR_LIKED.getDesc(), "encourageScore", EncorageBehaviorEnum.BEHAVIOR_LIKED.getEncourageScore()),
                Map.of("behaviorType", EncorageBehaviorEnum.BEHAVIOR_FEATURE.getDesc(), "encourageScore", EncorageBehaviorEnum.BEHAVIOR_FEATURE.getEncourageScore()),
                Map.of("behaviorType", EncorageBehaviorEnum.BEHAVIOR_COMMENT.getDesc(), "encourageScore", EncorageBehaviorEnum.BEHAVIOR_COMMENT.getEncourageScore()),
                Map.of("behaviorType", EncorageBehaviorEnum.BEHAVIOR_NEW_FAN.getDesc(), "encourageScore", EncorageBehaviorEnum.BEHAVIOR_NEW_FAN.getEncourageScore())
        );
    }

    @Override
    public PageBean<EncourageLogVo> getEncourageLog(RequestListDTO data, Long userId) {
        Long count = encourageLogMapper.selectCount(new LambdaQueryWrapper<EncourageLogDo>()
                .eq(EncourageLogDo::getUserId, userId));
        if (count == 0) {
            return new PageBean<>(false, 0, List.of());
        }
        List<EncourageLogDo> encourageLogList = encourageLogMapper.selectList(new LambdaQueryWrapper<EncourageLogDo>()
                .eq(EncourageLogDo::getUserId, userId)
                .orderByDesc(EncourageLogDo::getCreateTime)
                .last(" limit " + data.getOffset() + "," + data.getLimit()));
        if (CollectionUtils.isEmpty(encourageLogList)) {
            return new PageBean<>(false, Math.toIntExact(count), List.of());
        }
        List<EncourageLogVo> collect = encourageLogList.stream()
                .map(encourageLogDo -> {
                    EncourageLogVo encourageLogVo = new EncourageLogVo();
                    BeanUtil.copyProperties(encourageLogDo, encourageLogVo);
                    int behaviorType = encourageLogDo.getBehaviorType();
                    EncorageBehaviorEnum encourageEnum = EncorageBehaviorEnum.getEncourageEnum(behaviorType);
                    String behaviorMark = null;
                    if (encourageEnum == null) {
                        behaviorMark = "获得电池激励";
                    } else {
                        switch (encourageEnum) {
                            case BEHAVIOR_PUBLISH -> behaviorMark = "发表了文章";
                            case BEHAVIOR_LIKED -> behaviorMark = "获得一个赞";
                            case BEHAVIOR_FEATURE -> behaviorMark = "文章入选精选";
                            case BEHAVIOR_COMMENT -> behaviorMark = "获得一条评论";
                            case BEHAVIOR_NEW_FAN -> behaviorMark = "新增粉丝";
                        }
                    }
                    encourageLogVo.setBehaviorMark(behaviorMark);
                    return encourageLogVo;
                })
                .collect(Collectors.toList());
        return new PageBean<>(collect.size() == data.getLimit(), Math.toIntExact(count), collect);
    }

    /**
     * @return String
     * @description 获取一个全局唯一的domain
     * @param[1] userId
     * @author lovbe0210
     * @date 2024/11/17 22:01
     */
    public String createDomain(Long userId) {
        String domainKey = RedisKeyConstant.getDomainKey();
        String domain = IdUtil.nanoId(6);
        boolean hsetted = !properties.getDomainFilter().contains(domain) && RedisUtil.hsetIfAbsent(domainKey, domain, userId);
        while (!hsetted) {
            domain = IdUtil.nanoId(6);
            hsetted = !properties.getDomainFilter().contains(domain) && RedisUtil.hsetIfAbsent(domainKey, domain, userId);
        }
        return domain;
    }
}
