package com.lovbe.icharge.controller;

import com.lovbe.icharge.model.base.BaseRequest;
import com.lovbe.icharge.model.base.ResponseBean;
import com.lovbe.icharge.model.vo.UserInfoVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户信息相关控制层
 * @author: Lvhl
 * @date: 2024/7/9 11:33
 */
@RestController
public class UserController {
    public ResponseBean createUser(@RequestBody BaseRequest<UserInfoVo> userInfo) {
        return ResponseBean.ok();
    }
}
