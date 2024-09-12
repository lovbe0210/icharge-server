package com.lovbe.icharge.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.LoginLogTypeEnum;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.common.model.resp.AuthLoginRespVo;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.entity.vo.*;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.enums.LoginResultEnum;
import com.lovbe.icharge.service.AuthService;
import com.lovbe.icharge.service.feign.UserService;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


@Slf4j
@Service
public class AuthLoginServiceImpl implements AuthService {
    @Resource
    private UserService userService;
    @Resource
    private CommonService commonService;

    @Override
    public AuthLoginRespVo smsLogin(BaseRequest<AuthSmsLoginReqVo> reqVo) {
        // 校验验证码
        String userIp = ServletUtils.getClientIP();
        AuthSmsLoginReqVo data = reqVo.getData();
        SimpleCodeReqDTO simpleCodeReqDTO = new SimpleCodeReqDTO()
                .setMobile(data.getMobile())
                .setCode(data.getCode())
                .setScene(CodeSceneEnum.MOBILE_LOGIN)
                .setUsedIp(userIp);
        userService.useVerifyCode(new BaseRequest<>(simpleCodeReqDTO));

        // 获取用户信息
        AuthUserDTO authUserDTO = new AuthUserDTO()
                .setMobile(data.getMobile())
                .setLoginType(LoginLogTypeEnum.LOGIN_SMS_CODE.getType())
                .setUserIp(userIp);
        ResponseBean<LoginUser> userInfoResp = userService.createUserIfAbsent(new BaseRequest<>());
        // 生成token信息
        return getAuthLoginRespVo(userInfoResp, data.getMobile(), null, LoginLogTypeEnum.LOGIN_SMS_CODE);
    }

    @Override
    public AuthLoginRespVo mobileLogin(BaseRequest<AuthMobileLoginReqVo> reqVo) {
        // 使用手机 + 密码，进行登录。
        AuthMobileLoginReqVo data = reqVo.getData();
        String userIp = ServletUtils.getClientIP();
        AuthUserDTO authUserDTO = new AuthUserDTO()
                .setMobile(data.getMobile())
                .setLoginType(LoginLogTypeEnum.LOGIN_MOBILE_PASSWORD.getType())
                .setUserIp(userIp);
        ResponseBean<LoginUser> userInfoResp = userService.getLoginUserByPayload(new BaseRequest<>(authUserDTO));
        // 生成token信息
        return getAuthLoginRespVo(userInfoResp, data.getMobile(), data.getPassword(), LoginLogTypeEnum.LOGIN_MOBILE_PASSWORD);
    }

    @Override
    public AuthLoginRespVo emailCodeLogin(BaseRequest<AuthEmailCodeLoginReqVo> reqVo) {
        // 校验验证码
        String userIp = ServletUtils.getClientIP();
        AuthEmailCodeLoginReqVo data = reqVo.getData();
        SimpleCodeReqDTO simpleCodeReqDTO = new SimpleCodeReqDTO()
                .setEmail(data.getEmail())
                .setCode(data.getCode())
                .setScene(CodeSceneEnum.EMAIL_LOGIN)
                .setUsedIp(userIp);
        userService.useVerifyCode(new BaseRequest<>(simpleCodeReqDTO));

        // 获得注册用户
        AuthUserDTO authUserDTO = new AuthUserDTO()
                .setEmail(data.getEmail())
                .setLoginType(LoginLogTypeEnum.LOGIN_EMAIL_CODE.getType())
                .setUserIp(userIp);
        ResponseBean<LoginUser> userInfoResp = userService.createUserIfAbsent(new BaseRequest<>(authUserDTO));
        return getAuthLoginRespVo(userInfoResp, data.getEmail(), null, LoginLogTypeEnum.LOGIN_EMAIL_CODE);
    }


    @Override
    public AuthLoginRespVo emailLogin(BaseRequest<AuthEmailLoginReqVo> reqVo) {
        // 使用邮箱 + 密码，进行登录。
        AuthEmailLoginReqVo data = reqVo.getData();
        String userIp = ServletUtils.getClientIP();
        AuthUserDTO authUserDTO = new AuthUserDTO()
                .setEmail(data.getEmail())
                .setLoginType(LoginLogTypeEnum.LOGIN_EMAIL_PASSWORD.getType())
                .setUserIp(userIp);
        ResponseBean<LoginUser> userInfoResp = userService.getLoginUserByPayload(new BaseRequest<>(authUserDTO));
        // 创建 Token 令牌，记录登录日志
        return getAuthLoginRespVo(userInfoResp, data.getEmail(), data.getPassword(), LoginLogTypeEnum.LOGIN_EMAIL_PASSWORD);
    }

    @Override
    public ResponseBean sendSmsCode(BaseRequest<SmsCodeReqVo> reqVo) {
        SmsCodeReqVo data = reqVo.getData();
        Assert.notNull(data.getCodeScene(), GlobalErrorCodes.ERROR_CONFIGURATION.getMsg());
        // 滑块验证 TODO
        String sliderVerification = data.getSliderVerification();
        String actionDesc = data.getCodeScene().getDescription() + "获取验证码-滑块验证";
        ResponseBean sVResp = commonService.getSliderVerifyCode(sliderVerification, data.getMobile(), actionDesc);
        if (!sVResp.isResult()) {
            return sVResp;
        }

        // 发送验证码
        SimpleCodeReqDTO codeReqDTO = new SimpleCodeReqDTO()
                .setMobile(data.getMobile())
                .setScene(data.getCodeScene())
                .setUsedIp(ServletUtils.getClientIP());
        return ResponseBean.ok(MapUtil.of("code", userService.sendSmsCode(new BaseRequest<>(codeReqDTO))));
    }


    /**
     * @description 校验用户身份信息， 生成认证登录信息
     * @param[1] userInfoResp
     * @param[2] loginPayload
     * @param[3] password
     * @param[4] logType
     * @return AuthLoginRespVo
     * @author lovbe0210
     * @date 2024/8/14 22:40
     */
    private AuthLoginRespVo getAuthLoginRespVo(ResponseBean<LoginUser> userInfoResp,
                                               String loginPayload,
                                               String password,
                                               LoginLogTypeEnum logType) {
        if (userInfoResp == null || !userInfoResp.isResult()) {
            throw new ServiceException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
        }

        LoginUser user = userInfoResp.getData();
        if (user == null) {
            throw new ServiceException(
                    LoginLogTypeEnum.LOGIN_MOBILE_PASSWORD.equals(logType) ? ServiceErrorCodes.USER_MOBILE_NOT_EXISTS
                            : LoginLogTypeEnum.LOGIN_EMAIL_PASSWORD.equals(logType) ? ServiceErrorCodes.USER_EMAIL_NOT_EXISTS
                            : ServiceErrorCodes.AUTH_FAILED);
        }

        // 校验是否禁用
        if (!CommonStatusEnum.isNormal(user.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.AUTH_LOGIN_USER_DISABLED);
        }

        // 校验密码是否正确
        if (StringUtils.hasLength(password) && !isPasswordMatch(user.getPassword(), password)) {
            recordLoginLog(user.getUid(), loginPayload, logType.getDesc(), LoginResultEnum.BAD_CREDENTIALS);
            throw new ServiceException(ServiceErrorCodes.AUTH_LOGIN_BAD_CREDENTIALS);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getUid(), loginPayload, logType);
    }

    /**
     * @description 账号密码校验，需要先解密，然后加密进行对比
     * @param[1] dataPassword
     * @param[2] password
     * @return boolean
     * @author lovbe0210
     * @date 2024/8/14 22:44
     */
    private boolean isPasswordMatch(String dataPassword, String password) {
        // TODO
        return false;
    }

    /**
     * @description 登录成功，生成token信息
     * @param[1] userId
     * @param[2] mobileOrEmail
     * @param[3] logType
     * @return AuthLoginRespVo
     * @author lovbe0210
     * @date 2024/8/14 22:46
     */
    private AuthLoginRespVo createTokenAfterLoginSuccess(Long userId, String mobileOrEmail, LoginLogTypeEnum logType) {
        recordLoginLog(userId, mobileOrEmail, logType.getDesc(), LoginResultEnum.SUCCESS);
        // 创建 Token 令牌
        // accessToken 30分钟
        String accessToken = IdUtil.fastSimpleUUID();
        String key = RedisKeyConstant.getAccessTokenKey(accessToken);
        RedisUtil.set(key, userId, RedisKeyConstant.EXPIRE_30_MIN);
        // refreshToken 30天
        String refreshToken = IdUtil.fastSimpleUUID();
        key = RedisKeyConstant.getRefreshTokenKey(refreshToken);
        RedisUtil.set(key, userId, RedisKeyConstant.EXPIRE_30_MIN);
        // 构建返回结果
        return AuthLoginRespVo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .build();
    }

    /**
     * @description 记录登录日志
     * @param[1] uid
     * @param[2] mobile
     * @param[3] logTypeEnum
     * @param[4] loginResultEnum
     * @author lovbe0210
     * @date 2024/8/14 22:47
     */
    private void recordLoginLog(Long uid, String payload, String actionDesc, LoginResultEnum loginResultEnum) {
        // 插入登陆日志 TODO
        String userIp = ServletUtils.getClientIP();
        log.info("[Login] - userId: {}, LoginLogType：{}, userIp: {}", uid, loginResultEnum, userIp);
    }


}
