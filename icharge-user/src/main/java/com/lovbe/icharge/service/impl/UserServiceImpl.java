package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.common.config.ServiceProperties;
import com.lovbe.icharge.entity.dto.BatchUserRequestDTO;
import com.lovbe.icharge.entity.dto.DomainContentUpdateDTO;
import com.lovbe.icharge.entity.dto.UpdateUserDTO;
import com.lovbe.icharge.dao.UserMapper;
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
    public List<UserInfoDo> getUserInfoList(BaseRequest<BatchUserRequestDTO> batchRequest) {
        List<Long> userIdList = batchRequest.getData().getUserIdList();
        if (userIdList.size() > properties.getBatchSize()) {
            userIdList = userIdList.subList(0, properties.getBatchSize());
        }
        List<UserInfoDo> userInfoList = userMapper.selectBatchIds(userIdList);
        return CollectionUtils.isEmpty(userInfoList) ? List.of() : userInfoList;
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
