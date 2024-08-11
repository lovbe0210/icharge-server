package com.lovbe.icharge.service.impl;

import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.exception.ServiceErrorCodeConstants;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.common.util.FeignRequestUtils;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.entity.dto.AuthCodeReqDTO;
import com.lovbe.icharge.entity.vo.AuthEmailCodeLoginReqVo;
import com.lovbe.icharge.entity.vo.AuthEmailLoginReqVo;
import com.lovbe.icharge.entity.vo.AuthMobileLoginReqVo;
import com.lovbe.icharge.entity.vo.AuthSmsLoginReqVo;
import com.lovbe.icharge.entity.vo.resp.AuthLoginRespVo;
import com.lovbe.icharge.enums.CodeSceneEnum;
import com.lovbe.icharge.enums.LoginLogTypeEnum;
import com.lovbe.icharge.enums.LoginResultEnum;
import com.lovbe.icharge.service.AuthCodeService;
import com.lovbe.icharge.service.AuthService;
import com.lovbe.icharge.service.feign.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AuthLoginServiceImpl implements AuthService {
    @Resource
    private AuthCodeService codeService;
    @Resource
    private UserService userService;

    @Override
    public AuthLoginRespVo smsLogin(BaseRequest<AuthSmsLoginReqVo> reqVo) {
        // 校验验证码
        String userIp = ServletUtils.getClientIP();
        AuthSmsLoginReqVo data = reqVo.getData();
        codeService.useSmsCode(new AuthCodeReqDTO()
                                    .setMobile(data.getMobile())
                                    .setCode(data.getCode())
                                    .setScene(CodeSceneEnum.MOBILE_LOGIN)
                                    .setUsedIp(userIp));

        // 获得获得注册用户
        ResponseBean<LoginUser> userInfoResp = userService.createUserIfAbsent(data.getMobile(), LoginLogTypeEnum.LOGIN_SMS_CODE.getType(), userIp);
        if (!FeignRequestUtils.checkResp(userInfoResp)) {
           throw new ServiceException(ServiceErrorCodeConstants.USER_NOT_EXISTS);
        }
        LoginUser user = userInfoResp.getData();

        // 校验是否禁用
        if (!CommonStatusEnum.isNormal(user.getStatus())) {
            throw new ServiceException(ServiceErrorCodeConstants.AUTH_LOGIN_USER_DISABLED);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getUid(), data.getMobile(), LoginLogTypeEnum.LOGIN_SMS_CODE);
    }

    @Override
    public AuthLoginRespVo mobileLogin(BaseRequest<AuthMobileLoginReqVo> reqVo) {
        // 使用手机 + 密码，进行登录。
        AuthMobileLoginReqVo data = reqVo.getData();
        LoginUser user = login0(data.getMobile(), data.getPassword());
        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getUid(), data.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE_PASSWORD);
    }

    @Override
    public AuthLoginRespVo emailCodeLogin(BaseRequest<AuthEmailCodeLoginReqVo> reqVo) {
        // 校验验证码
        String userIp = ServletUtils.getClientIP();
        AuthEmailCodeLoginReqVo data = reqVo.getData();
        codeService.useSmsCode(new AuthCodeReqDTO()
                .setEmail(data.getEmail())
                .setCode(data.getCode())
                .setScene(CodeSceneEnum.EMAIL_LOGIN)
                .setUsedIp(userIp));

        // 获得注册用户
        ResponseBean<LoginUser> userInfoResp = userService.createUserIfAbsent(data.getEmail(), LoginLogTypeEnum.LOGIN_EMAIL_CODE.getType(), userIp);
        if (!FeignRequestUtils.checkResp(userInfoResp)) {
            throw new ServiceException(ServiceErrorCodeConstants.USER_NOT_EXISTS);
        }
        LoginUser user = userInfoResp.getData();

        // 校验是否禁用
        if (!CommonStatusEnum.isNormal(user.getStatus())) {
            throw new ServiceException(ServiceErrorCodeConstants.AUTH_LOGIN_USER_DISABLED);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getUid(), data.getEmail(), LoginLogTypeEnum.LOGIN_SMS_CODE);
    }

    @Override
    public AuthLoginRespVo emailLogin(BaseRequest<AuthEmailLoginReqVo> reqVo) {
        // 使用邮箱 + 密码，进行登录。
        AuthEmailLoginReqVo data = reqVo.getData();
        LoginUser user = login1(data.getMobile(), data.getPassword());
        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getUid(), data.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE_PASSWORD);
    }

    private AuthLoginRespVo createTokenAfterLoginSuccess(Integer userId, String mobileOrEmail, LoginLogTypeEnum logType) {
        createLoginLog(userId, mobileOrEmail, logType, LoginResultEnum.SUCCESS);
        // 创建 Token 令牌

        // 构建返回结果
        return AuthLoginRespVo.builder().accessToken("").build();
    }

    private void createLoginLog(Integer uid, String mobile, LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResultEnum) {
        // 插入登陆日志 TODO
        String userIp = ServletUtils.getClientIP();
        log.info("[Login] - userId: {}, LoginLogType：{}, userIp: {}", uid, loginResultEnum, userIp);
    }

    private boolean isPasswordMatch(String dataPassword, String password) {

        return false;
    }

    /**
     * 使用手机号密码登录
     * @param mobile
     * @param password
     * @return
     */
    private LoginUser login0(String mobile, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_MOBILE_PASSWORD;
        // 校验账号是否存在
        ResponseBean<LoginUser> userInfoResp = userService.getLoginUserByMobile(mobile);
        if (!FeignRequestUtils.checkResp(userInfoResp)) {
            throw new ServiceException(ServiceErrorCodeConstants.USER_NOT_EXISTS);
        }
        LoginUser user = userInfoResp.getData();

        // 校验是否禁用
        if (!CommonStatusEnum.isNormal(user.getStatus())) {
            throw new ServiceException(ServiceErrorCodeConstants.AUTH_LOGIN_USER_DISABLED);
        }

        // 校验密码是否正确
        if (!isPasswordMatch(user.getPassword(), password)) {
            createLoginLog(user.getUid(), mobile, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw new ServiceException(ServiceErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS);
        }
        return user;
    }

    /**
     * 使用邮箱密码登录
     * @param email
     * @param password
     * @return
     */
    private LoginUser login1(String email, String password) {
        // 校验账号是否存在
        ResponseBean<LoginUser> userInfoResp = userService.getLoginUserByEmail(email);
        if (!FeignRequestUtils.checkResp(userInfoResp)) {
            throw new ServiceException(ServiceErrorCodeConstants.USER_NOT_EXISTS);
        }
        LoginUser user = userInfoResp.getData();

        // 校验是否禁用
        if (!CommonStatusEnum.isNormal(user.getStatus())) {
            throw new ServiceException(ServiceErrorCodeConstants.AUTH_LOGIN_USER_DISABLED);
        }

        // 校验密码是否正确
        if (!isPasswordMatch(user.getPassword(), password)) {
            createLoginLog(user.getUid(), email, LoginLogTypeEnum.LOGIN_EMAIL_PASSWORD, LoginResultEnum.BAD_CREDENTIALS);
            throw new ServiceException(ServiceErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS);
        }
        return user;
    }
}
