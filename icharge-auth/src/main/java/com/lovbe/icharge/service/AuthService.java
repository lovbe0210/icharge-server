package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.entity.vo.AuthEmailCodeLoginReqVo;
import com.lovbe.icharge.entity.vo.AuthEmailLoginReqVo;
import com.lovbe.icharge.entity.vo.AuthMobileLoginReqVo;
import com.lovbe.icharge.entity.vo.resp.AuthLoginRespVo;
import com.lovbe.icharge.entity.vo.AuthSmsLoginReqVo;

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
    AuthLoginRespVo smsLogin(BaseRequest<AuthSmsLoginReqVo> reqVo);

    /**
     * description: 手机号密码登录
     * @author: Lvhl
     * @date: 2024/8/7 13:53
     * @param reqVo
     * @return AuthLoginRespVo
     */
    AuthLoginRespVo mobileLogin(BaseRequest<AuthMobileLoginReqVo> reqVo);

    /**
     * description: 邮箱验证码登录
     * @author: Lvhl
     * @date: 2024/8/7 13:53
     * @param reqVo
     * @return AuthLoginRespVo
     */
    AuthLoginRespVo emailCodeLogin(BaseRequest<AuthEmailCodeLoginReqVo> reqVo);

    /**
     * description: 邮箱密码登录
     * @author: Lvhl
     * @date: 2024/8/7 13:53
     * @param reqVo
     * @return AuthLoginRespVo
     */
    AuthLoginRespVo emailLogin(BaseRequest<AuthEmailLoginReqVo> reqVo);
}
