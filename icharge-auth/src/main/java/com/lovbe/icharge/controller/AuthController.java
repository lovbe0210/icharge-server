package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.resp.AuthLoginUser;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.dto.vo.AuthEmailCodeLoginReqVo;
import com.lovbe.icharge.dto.vo.AuthEmailLoginReqVo;
import com.lovbe.icharge.dto.vo.AuthMobileLoginReqVo;
import com.lovbe.icharge.dto.vo.AuthSmsLoginReqVo;
import com.lovbe.icharge.service.AuthService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


/**
 * @description: 权限认证
 * @author: Lvhl
 * @date: 2024/8/2 15:21
 */
@RefreshScope
@RestController
public class AuthController {
    @Resource
    private AuthService authService;
    @Value("${global.param.domain}")
    private String domain;
    @Value("${global.param.secure}")
    private boolean secure;

    /**
     * description: 手机验证码登录
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/sms/login")
    public ResponseBean smsLogin(@RequestBody @Valid BaseRequest<AuthSmsLoginReqVo> reqVo,
                                 HttpServletResponse response) {
        AuthLoginUser loginUser = authService.smsLogin(reqVo);
        ServletUtils.setLoginCookie(domain, secure, response, loginUser);
        return ResponseBean.ok(loginUser);
    }

    /**
     * description: 手机密码登录
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/mobile/login")
    public ResponseBean mobileLogin(@RequestBody @Valid BaseRequest<AuthMobileLoginReqVo> reqVo,
                                    HttpServletResponse response) {
        AuthLoginUser loginUser = authService.mobileLogin(reqVo);
        ServletUtils.setLoginCookie(domain, secure, response, loginUser);
        return ResponseBean.ok(loginUser);
    }

    /**
     * description: 邮箱验证码登录
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/email-code/login")
    public ResponseBean emailCodeLogin(@RequestBody @Valid BaseRequest<AuthEmailCodeLoginReqVo> reqVo,
                                       HttpServletResponse response) {
        AuthLoginUser loginUser = authService.emailCodeLogin(reqVo);
        ServletUtils.setLoginCookie(domain, secure, response, loginUser);
        return ResponseBean.ok(loginUser);
    }

    /**
     * description: 邮箱密码登录
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/email/login")
    public ResponseBean emailLogin(@RequestBody @Valid BaseRequest<AuthEmailLoginReqVo> reqVo,
                                   HttpServletResponse response) {
        AuthLoginUser loginUser = authService.emailLogin(reqVo);
        ServletUtils.setLoginCookie(domain, secure, response, loginUser);
        return ResponseBean.ok(loginUser);
    }

    /**
     * description: 退出登录
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param rfToken
     * @return ResponseBean
     */
    @PostMapping("/logout")
    public ResponseBean logout(@RequestHeader("icharge-rt") String rfToken) {
        authService.logout(rfToken);
        return ResponseBean.ok();
    }

    /**
     * description: 刷新token
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param rfToken
     * @return ResponseBean
     */
    @PostMapping("/t/refresh")
    public ResponseBean refreshToken(@RequestHeader("icharge-rt") String rfToken, HttpServletResponse response) {
        AuthLoginUser loginUser = authService.refreshToken(rfToken);
        ServletUtils.setLoginCookie(domain, secure, response, loginUser);
        return ResponseBean.ok();
    }

}
