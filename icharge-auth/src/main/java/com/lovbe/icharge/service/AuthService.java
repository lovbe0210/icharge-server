package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.entity.vo.resp.AuthLoginRespVo;
import com.lovbe.icharge.entity.vo.AuthMobileCodeLoginReqVo;

/**
 * @description: 登陆认证服务
 * @author: Lvhl
 * @date: 2024/8/7 13:43
 */
public interface AuthService {

    /**
     * description: 短信登录
     * @author: Lvhl
     * @date: 2024/8/7 13:53
     * @param reqVo
     * @return AuthLoginRespVo
     */
    AuthLoginRespVo smsLogin(BaseRequest<AuthMobileCodeLoginReqVo> reqVo);
}
