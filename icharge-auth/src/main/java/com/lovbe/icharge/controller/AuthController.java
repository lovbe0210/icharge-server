package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.vo.AuthEmailCodeLoginReqVo;
import com.lovbe.icharge.entity.vo.AuthEmailLoginReqVo;
import com.lovbe.icharge.entity.vo.AuthMobileLoginReqVo;
import com.lovbe.icharge.entity.vo.AuthSmsLoginReqVo;
import com.lovbe.icharge.service.AuthService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @description: 权限认证
 * @author: Lvhl
 * @date: 2024/8/2 15:21
 */
@RestController
public class AuthController {
    @Resource
    private AuthService authService;

    /**
     * description: 手机验证码登录
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/sms/login")
    public ResponseBean smsLogin(@RequestBody @Valid BaseRequest<AuthSmsLoginReqVo> reqVo) {
        return ResponseBean.ok(authService.smsLogin(reqVo));
    }

    /**
     * description: 手机密码登录
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/mobile/login")
    public ResponseBean mobileLogin(@RequestBody @Valid BaseRequest<AuthMobileLoginReqVo> reqVo) {
        return ResponseBean.ok(authService.mobileLogin(reqVo));
    }

    /**
     * description: 邮箱验证码登录
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/emailCode/login")
    public ResponseBean emailCodeLogin(@RequestBody @Valid BaseRequest<AuthEmailCodeLoginReqVo> reqVo) {
        return ResponseBean.ok(authService.emailCodeLogin(reqVo));
    }

    /**
     * description: 邮箱密码登录
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/email/login")
    public ResponseBean emailLogin(@RequestBody @Valid BaseRequest<AuthEmailLoginReqVo> reqVo) {
        return ResponseBean.ok(authService.emailLogin(reqVo));
    }

}
