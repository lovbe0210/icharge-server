package com.lovbe.icharge.dto.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * @description: 邮箱验证码登录
 * @author: Lvhl
 * @date: 2024/8/7 13:37
 */
@Data
public class AuthEmailCodeLoginReqVo {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Length(min = 6, max = 6, message = "验证码错误")
    @Pattern(regexp = "^[0-9]+$", message = "验证码错误")
    private String code;
}
