package com.lovbe.icharge.entity.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


/**
 * @description: 邮箱密码登录
 * @author: Lvhl
 * @date: 2024/8/7 13:37
 */
@Data
public class AuthEmailLoginReqVo {
    @NotEmpty(message = "邮箱不能为空")
    @Email
    private String mobile;

    @NotEmpty(message = "密码不能为空")
    private String password;
}
