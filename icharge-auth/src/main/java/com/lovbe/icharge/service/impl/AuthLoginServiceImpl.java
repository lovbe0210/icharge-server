package com.lovbe.icharge.service.impl;

import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.exception.ServiceErrorCodeConstants;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.common.model.entity.UserInfo;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
        ResponseBean<LoginUser> userInfoResp = userService.createUserIfAbsent(data.getMobile(), userIp);
        if (!FeignRequestUtils.checkResp(userInfoResp)) {
           throw new ServiceException(ServiceErrorCodeConstants.USER_NOT_EXISTS);
        }
        LoginUser user = userInfoResp.getData();

        // 校验是否禁用
        if (!CommonStatusEnum.isNormal(user.getStatus())) {
            throw new ServiceException(ServiceErrorCodeConstants.AUTH_LOGIN_USER_DISABLED);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getUid(), data.getMobile(), LoginLogTypeEnum.LOGIN_SMS_CODE, userIp);
    }

    @Override
    public AuthLoginRespVo mobileLogin(BaseRequest<AuthMobileLoginReqVo> reqVo) {
        // 使用手机 + 密码，进行登录。
        AuthMobileLoginReqVo data = reqVo.getData();
        LoginUser user = login0(data.getMobile(), data.getPassword());

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getUid(), data.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE_PASSWORD, null);
    }

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

    private boolean isPasswordMatch(String dataPassword, String password) {

        return false;
    }

    @Override
    public AuthLoginRespVo emailCodeLogin(BaseRequest<AuthEmailCodeLoginReqVo> reqVo) {
        return null;
    }

    @Override
    public AuthLoginRespVo emailLogin(BaseRequest<AuthEmailLoginReqVo> reqVo) {
        return null;
    }

    private AuthLoginRespVo createTokenAfterLoginSuccess(Integer userId, String mobile, LoginLogTypeEnum logType, String userIp) {
        createLoginLog(userId, mobile, logType, LoginResultEnum.SUCCESS);
        // 创建 Token 令牌

        // 构建返回结果
        return AuthLoginRespVo.builder().accessToken("").build();
    }

    private void createLoginLog(Integer uid, String mobile, LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResultEnum) {
        // 插入登陆日志 TODO
        log.info("[Login] - userId: {}, LoginLogType：{}", uid, loginResultEnum);
    }
}
