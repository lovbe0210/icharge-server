package com.lovbe.icharge.entity.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @description: 邮箱密码登录
 * @author: Lvhl
 * @date: 2024/8/7 13:37
 */
@Data
public class AuthEmailLoginReqVo {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;
}
