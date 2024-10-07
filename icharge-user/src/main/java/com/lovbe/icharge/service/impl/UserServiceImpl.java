package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.common.util.validation.ValidationUtils;
import com.lovbe.icharge.dto.ForgetPasswordDTO;
import com.lovbe.icharge.dto.UpdateUserDTO;
import com.lovbe.icharge.mapper.UserMapper;
import com.lovbe.icharge.service.AccountService;
import com.lovbe.icharge.service.UserService;
import com.lovbe.icharge.service.feign.StorageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private BCryptPasswordEncoder cryptPasswordEncoder;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginUser createUserIfAbsent(AuthUserDTO authUserDTO) {
        AccountDo account = accountService.getAccountInfo(authUserDTO);
        if (account != null) {
            // 记录登录日志
            account.setLastLoginIp(authUserDTO.getUserIp())
                    .setLastLoginTime(LocalDateTime.now())
                    .setLoginAgent(authUserDTO.getLoginAgent())
                    .setLoginOs(authUserDTO.getLoginOs())
                    .setLoginCount(account.getLoginCount() + 1)
                    .setUpdateTime(LocalDateTime.now());
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
                .setLastLoginTime(LocalDateTime.now());
        account.setUid(uid);
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        accountService.createAccount(account);
        // 创建用户
        UserInfoDo userInfoDo = new UserInfoDo()
                .setUsername("用户" + IdUtil.nanoId(4))
                .setDomain(IdUtil.nanoId(6));
        userInfoDo.setUid(uid);
        userInfoDo.setCreateTime(LocalDateTime.now());
        userInfoDo.setUpdateTime(LocalDateTime.now());
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
                    .setLastLoginTime(LocalDateTime.now())
                    .setLoginAgent(authUserDTO.getLoginAgent())
                    .setLoginOs(authUserDTO.getLoginOs())
                    .setLoginCount(account.getLoginCount() + 1)
                    .setUpdateTime(LocalDateTime.now());
            int updated = accountService.updateAccount(account);
            LoginUser loginUser = new LoginUser(account.getMobile(), account.getEmail(), account.getPassword());
            loginUser.setStatus(account.getStatus());
            loginUser.setUid(account.getUid());
            return loginUser;
        }
        return null;
    }

    @Override
    public void resetUserPwd(ForgetPasswordDTO data) {
        // 校验验证码是否正确
        boolean isMobile = CodeSceneEnum.sceneIsMobile(data.getScene());
        String payload = isMobile ? data.getMobile() : data.getEmail();
        String codeExpireKey = RedisKeyConstant.getCodeControlKey(payload);
        Object codeExpire = RedisUtil.hget(codeExpireKey, data.getVerifyCode());
        if (codeExpire == null) {
            throw new ServiceException(ServiceErrorCodes.AUTH_CODE_ERROR);
        }
        // 中间的为过期时间
        String[] split = ((String) codeExpire).split("_");
        Long expire = Long.valueOf(split[1]);
        if (System.currentTimeMillis() > expire) {
            throw new ServiceException(ServiceErrorCodes.AUTH_CODE_EXPIRED);
        }

        // 判断用户是否有效
        AccountDo account = accountService.getAccountByMobileOrEmail(data);
        if (account == null) {
            throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
        }

        // 密码加密入库
        String decodedPassword = Base64.decodeStr(ValidationUtils.bitwiseInvert(data.getPassword()));
        String encodePassword = cryptPasswordEncoder.encode(decodedPassword);
        account.setPassword(encodePassword);
        int updated = accountService.updateAccount(account);
        if (updated == 0) {
            account.setPassword("***").setMobile("***").setEmail("***");
            log.error("[重置密码] --- 密码重置失败，account: {}", account);
            throw new ServiceException(ServiceErrorCodes.ACCOUNT_PASSWORD_RESET_FAILED);
        }
    }

    @Override
    public ResponseBean getUserInfo(Long userId) {
        UserInfoDo userInfoDo = userMapper.selectById(userId);
        if (userInfoDo == null) {
            throw new ServiceException(ServiceErrorCodes.USER_NOT_EXIST);
        }
        if (!CommonStatusEnum.NORMAL.getStatus().equals(userInfoDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.USER_DISABLED);
        }
        return ResponseBean.ok(userInfoDo);
    }

    @Override
    public void updateUserInfo(Long userId, UpdateUserDTO userDTO) {
        // 业务参数校验
        String tags = userDTO.getTagArray();
        if (tags != null && tags.length() > 200) {
            throw new ServiceException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
        }
        UserInfoDo userInfo = new UserInfoDo();
        userInfo.setUid(userId);
        userInfo.setTags(JSONUtil.toList(tags, Map.class));
        BeanUtil.copyProperties(userDTO, userInfo);
        MultipartFile avatarFile = userDTO.getAvatarFile();
        if (avatarFile != null) {
            ResponseBean<String> upload = storageService.upload(avatarFile, SysConstant.FILE_SCENE_AVATAR);
            if (!upload.isResult()) {
                log.error("[更新用户信息] --- 头像上传失败，errorInfo: {}", upload.getMessage());
                throw new ServiceException(ServiceErrorCodes.USER_INFO_UPDATE_FAILED);
            }
            userInfo.setAvatarUrl(upload.getData());
        }
        userMapper.updateById(userInfo);
    }
}
