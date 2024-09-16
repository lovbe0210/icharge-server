package com.lovbe.icharge.dto.vo;

import com.lovbe.icharge.common.util.validation.ValidationUtils;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = ValidationUtils.MOBILE_REGEXP, message = "手机号格式不正确")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    @Length(min = 6, max = 6, message = "验证码长度为6位")
    @Pattern(regexp = "^[0-9]+$", message = "验证码必须都是数字")
    private String code;
}
