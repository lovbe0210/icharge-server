package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户信息相关控制层
 * @author: Lvhl
 * @date: 2024/7/9 11:33
 */
@RestController
public class UserController {
    @Resource
    private UserService userService;


    @PostMapping("/createUserIfAbsent")
    public ResponseBean<LoginUser> createUserIfAbsent(@RequestBody @Valid AuthUserDTO authUserDTO) {
        return ResponseBean.ok(userService.createUserIfAbsent(authUserDTO));
    }

    @PostMapping("/getUser/loginPayload")
    public ResponseBean<LoginUser> getLoginUserByPayload(@RequestBody AuthUserDTO authUserDTO) {
        return ResponseBean.ok(userService.getLoginUserByPayload(authUserDTO));
    }
}
