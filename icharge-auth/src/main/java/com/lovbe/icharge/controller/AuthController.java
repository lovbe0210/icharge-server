package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.vo.AuthMobileCodeLoginReqVo;
import com.lovbe.icharge.service.AuthService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @description: 权限认证
 * @author: Lvhl
 * @date: 2024/8/2 15:21
 */
@RequestMapping("/auth")
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
    @RequestMapping("/smsLogin")
    public ResponseBean smsLogin(@RequestBody @Valid BaseRequest<AuthMobileCodeLoginReqVo> reqVo) {
        return ResponseBean.ok(authService.smsLogin(reqVo));
    }

}
