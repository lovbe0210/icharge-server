package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.validation.Mobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @description: 邮箱验证码登录
 * @author: Lvhl
 * @date: 2024/8/7 13:37
 */
@Data
public class AuthEmailCodeLoginReqVo {
    @NotEmpty(message = "邮箱不能为空")
    @Email
    private String email;

    @NotEmpty(message = "验证码不能为空")
    @Length(min = 6, max = 6, message = "验证码长度为 6 位")
    @Pattern(regexp = "^[0-9]+$", message = "验证码必须都是数字")
    private String code;
}
