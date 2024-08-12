package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户信息相关控制层
 * @author: Lvhl
 * @date: 2024/7/9 11:33
 */
@RestController("/api")
public class UserController {
    @PostMapping("/user/createUserIfAbsent")
    public ResponseBean<LoginUser> createUserIfAbsent(@RequestBody AuthUserDTO authUserDTO) {
        return ResponseBean.ok(new LoginUser());
    }

    @PostMapping("/getUser/mobile")
    public ResponseBean<LoginUser> getLoginUserByMobile(@RequestBody AuthUserDTO authUserDTO) {
        return null;
    }

    @PostMapping("/getUser/email")
    public ResponseBean<LoginUser> getLoginUserByEmail(@RequestBody AuthUserDTO authUserDTO) {
        return null;
    }
}
