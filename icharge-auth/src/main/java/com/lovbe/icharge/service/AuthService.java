package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.vo.EmailCodeReqVo;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;
import com.lovbe.icharge.dto.vo.*;
import com.lovbe.icharge.common.model.resp.AuthLoginUser;

/**
 * @description: 登陆认证服务
 * @author: Lvhl
 * @date: 2024/8/7 13:43
 */
public interface AuthService {

    /**
     * description: 短信验证码登录
     * @author: Lvhl
     * @date: 2024/8/7 13:53
     * @param reqVo
     * @return AuthLoginRespVo
     */
    AuthLoginUser smsLogin(BaseRequest<AuthSmsLoginReqVo> reqVo);

    /**
     * description: 手机号密码登录
     * @author: Lvhl
     * @date: 2024/8/7 13:53
     * @param reqVo
     * @return AuthLoginRespVo
     */
    AuthLoginUser mobileLogin(BaseRequest<AuthMobileLoginReqVo> reqVo);

    /**
     * description: 邮箱验证码登录
     * @author: Lvhl
     * @date: 2024/8/7 13:53
     * @param reqVo
     * @return AuthLoginRespVo
     */
    AuthLoginUser emailCodeLogin(BaseRequest<AuthEmailCodeLoginReqVo> reqVo);

    /**
     * description: 邮箱密码登录
     * @author: Lvhl
     * @date: 2024/8/7 13:53
     * @param reqVo
     * @return AuthLoginRespVo
     */
    AuthLoginUser emailLogin(BaseRequest<AuthEmailLoginReqVo> reqVo);

    /**
     * @description 发送短信验证码
     * @param[1] reqVo
     * @author lovbe0210
     * @date 2024/9/26 23:36
     */
    void sendSmsCode(BaseRequest<SmsCodeReqVo> reqVo);

    /**
     * @description 发送邮箱验证码
     * @param[1] reqVo
     * @author lovbe0210
     * @date 2024/9/26 23:38
     */
    void sendEmailCode(BaseRequest<EmailCodeReqVo> reqVo);

    /**
     * @description 退出登录
     * @param[1] data
     * @author lovbe0210
     * @date 2024/9/28 22:43
     */
    void logout(String rfToken);

    /**
     * @description 通过refreshToken获取一个新的accessToken
     * @param[1] rfToken
     * @return AuthLoginUser
     * @author lovbe0210
     * @date 2024/10/7 19:08
     */
    AuthLoginUser refreshToken(String rfToken);
}
