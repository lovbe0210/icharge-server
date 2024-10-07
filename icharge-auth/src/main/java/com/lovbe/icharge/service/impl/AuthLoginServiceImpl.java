package com.lovbe.icharge.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.LoginLogTypeEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.common.model.resp.AuthLoginUser;
import com.lovbe.icharge.common.model.vo.EmailCodeReqVo;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.util.validation.ValidationUtils;
import com.lovbe.icharge.dto.vo.*;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.enums.LoginResultEnum;
import com.lovbe.icharge.service.AuthService;
import com.lovbe.icharge.service.feign.UserService;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AuthLoginServiceImpl implements AuthService {
    @Resource
    private UserService userService;
    @Resource
    private CommonService commonService;
    @Resource
    private BCryptPasswordEncoder cryptPasswordEncoder;

    @Override
    public AuthLoginUser smsLogin(BaseRequest<AuthSmsLoginReqVo> reqVo) {
        // 校验验证码
        String userIp = ServletUtils.getClientIP();
        AuthSmsLoginReqVo data = reqVo.getData();
        SimpleCodeReqDTO simpleCodeReqDTO = new SimpleCodeReqDTO()
                .setMobile(data.getMobile())
                .setCode(data.getCode())
                .setScene(CodeSceneEnum.MOBILE_LOGIN.getScene())
                .setUsedIp(userIp);
        ResponseBean responseBean = userService.useVerifyCode(new BaseRequest<>(simpleCodeReqDTO));
        if (!responseBean.isResult()) {
            throw new ServiceException(responseBean.getMessage());
        }

        // 获取用户信息
        AuthUserDTO authUserDTO = new AuthUserDTO()
                .setMobile(data.getMobile())
                .setLoginType(LoginLogTypeEnum.LOGIN_SMS_CODE.getType())
                .setUserIp(userIp);
        ResponseBean<LoginUser> userInfoResp = userService.createUserIfAbsent(new BaseRequest<>(authUserDTO));
        // 生成token信息
        return getAuthLoginRespVo(userInfoResp, data.getMobile(), null, LoginLogTypeEnum.LOGIN_SMS_CODE);
    }

    @Override
    public AuthLoginUser mobileLogin(BaseRequest<AuthMobileLoginReqVo> reqVo) {
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
    public AuthLoginUser emailCodeLogin(BaseRequest<AuthEmailCodeLoginReqVo> reqVo) {
        // 校验验证码
        String userIp = ServletUtils.getClientIP();
        AuthEmailCodeLoginReqVo data = reqVo.getData();
        SimpleCodeReqDTO simpleCodeReqDTO = new SimpleCodeReqDTO()
                .setEmail(data.getEmail())
                .setCode(data.getCode())
                .setScene(CodeSceneEnum.EMAIL_LOGIN.getScene())
                .setUsedIp(userIp);
        ResponseBean responseBean = userService.useVerifyCode(new BaseRequest<>(simpleCodeReqDTO));
        if (!responseBean.isResult()) {
            throw new ServiceException(responseBean.getMessage());
        }

        // 获得注册用户
        AuthUserDTO authUserDTO = new AuthUserDTO()
                .setEmail(data.getEmail())
                .setLoginType(LoginLogTypeEnum.LOGIN_EMAIL_CODE.getType())
                .setUserIp(userIp);
        ResponseBean<LoginUser> userInfoResp = userService.createUserIfAbsent(new BaseRequest<>(authUserDTO));
        return getAuthLoginRespVo(userInfoResp, data.getEmail(), null, LoginLogTypeEnum.LOGIN_EMAIL_CODE);
    }


    @Override
    public AuthLoginUser emailLogin(BaseRequest<AuthEmailLoginReqVo> reqVo) {
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
    public void sendSmsCode(BaseRequest<SmsCodeReqVo> reqVo) {
        SmsCodeReqVo data = reqVo.getData();
        SimpleCodeReqDTO codeReqDTO = new SimpleCodeReqDTO()
                .setMobile(data.getMobile())
                .setScene(data.getScene())
                .setSign(data.getSign());
        ResponseBean responseBean = userService.sendSmsCode(new BaseRequest<>(codeReqDTO));
        if (!responseBean.isResult()) {
            int code = responseBean.getCode();
            if (code == 400) {
                throw new ServiceException(responseBean.getMessage());
            }
        }
    }

    @Override
    public void sendEmailCode(BaseRequest<EmailCodeReqVo> reqVo) {
        EmailCodeReqVo data = reqVo.getData();
        SimpleCodeReqDTO codeReqDTO = new SimpleCodeReqDTO()
                .setEmail(data.getEmail())
                .setScene(data.getScene().getScene())
                .setSign(data.getSign());
        ResponseBean responseBean = userService.sendEmailCode(new BaseRequest<>(codeReqDTO));
        if (!responseBean.isResult()) {
            int code = responseBean.getCode();
            if (code == 400) {
                throw new ServiceException(responseBean.getMessage());
            }
        }
    }

    @Override
    public void logout(String rfToken) {
        String refreshTokenKey = RedisKeyConstant.getRefreshTokenKey(rfToken);
        Map<Object, Object> map = RedisUtil.hgetMap(refreshTokenKey);
        if (!CollectionUtils.isEmpty(map)) {
            List<String> collect = map.keySet().stream()
                    .map(key -> RedisKeyConstant.getAccessTokenKey(String.valueOf(key)))
                    .collect(Collectors.toList());
            RedisUtil.del(collect.toArray(new String[]{}));
        }
        RedisUtil.del(refreshTokenKey);
    }

    @Override
    public AuthLoginUser refreshToken(String rfToken) {
        String refreshTokenKey = RedisKeyConstant.getRefreshTokenKey(rfToken);
        Map<Object, Object> map = RedisUtil.hgetMap(refreshTokenKey);
        if (CollectionUtils.isEmpty(map)) {
            throw new ServiceException(GlobalErrorCodes.ACCOUNT_LOGIN_EXPIRED);
        }
        // 设置accessToken的过期时间为30分钟
        String acToken = IdUtil.fastSimpleUUID();
        String accessTokenKey = RedisKeyConstant.getAccessTokenKey(acToken);
        RedisUtil.set(accessTokenKey, map.get(SysConstant.USERID), RedisKeyConstant.EXPIRE_30_MIN);
        // 只有当剩余时间大于60s时才回写accessToken，否则可能会造成refreshToken永久有效
        Long expire = RedisUtil.getExpire(refreshTokenKey);
        if (expire != null && expire > 60) {
            RedisUtil.hsetIfAbsent(refreshTokenKey, acToken, acToken);
        }
        return new AuthLoginUser()
                .setUserId((Long) map.get(SysConstant.USERID))
                .setAcToken(acToken);
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
    private AuthLoginUser getAuthLoginRespVo(ResponseBean<LoginUser> userInfoResp,
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
    private boolean isPasswordMatch(String dbPassword, String loginPassword) {
        // 先转码为原始密码
        String decodedPassword = Base64.decodeStr(ValidationUtils.bitwiseInvert(loginPassword));
        return cryptPasswordEncoder.matches(decodedPassword, dbPassword);
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
    private AuthLoginUser createTokenAfterLoginSuccess(Long userId, String mobileOrEmail, LoginLogTypeEnum logType) {
        recordLoginLog(userId, mobileOrEmail, logType.getDesc(), LoginResultEnum.SUCCESS);
        // 创建 Token 令牌
        // accessToken 30分钟
        String accessToken = IdUtil.fastSimpleUUID();
        String key = RedisKeyConstant.getAccessTokenKey(accessToken);
        RedisUtil.set(key, userId, RedisKeyConstant.EXPIRE_30_MIN);
        // refreshToken 30天
        String refreshToken = IdUtil.fastSimpleUUID();
        key = RedisKeyConstant.getRefreshTokenKey(refreshToken);
        RedisUtil.hset(key, SysConstant.USERID, userId, RedisKeyConstant.EXPIRE_30_DAY);
        RedisUtil.hset(key, accessToken, accessToken, RedisKeyConstant.EXPIRE_30_DAY);
        // 构建返回结果
        return AuthLoginUser.builder()
                .acToken(accessToken)
                .rfToken(refreshToken)
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
