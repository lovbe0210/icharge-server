package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.AuthInfoDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 权限认证
 * @author: Lvhl
 * @date: 2024/8/2 15:21
 */
@RequestMapping("/auth")
@RestController
public class AuthController {

    /**
     * description: 手机号登录
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param request
     * @return ResponseBean
     */
    @RequestMapping("/mobile-login")
    public ResponseBean mobileLogin(@RequestBody @Validated BaseRequest<AuthInfoDTO> request) {
        String mobile = request.getData().getMobile();
        return ResponseBean.ok();
    }
}
