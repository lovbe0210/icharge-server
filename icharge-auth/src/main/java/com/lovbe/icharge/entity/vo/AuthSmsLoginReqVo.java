package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.validation.Mobile;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * @description: 手机验证码登录
 * @author: Lvhl
 * @date: 2024/8/7 13:37
 */
@Data
public class AuthSmsLoginReqVo {
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @NotEmpty(message = "验证码不能为空")
    @Length(min = 6, max = 6, message = "验证码长度为 6 位")
    @Pattern(regexp = "^[0-9]+$", message = "验证码必须都是数字")
    private String code;
}
