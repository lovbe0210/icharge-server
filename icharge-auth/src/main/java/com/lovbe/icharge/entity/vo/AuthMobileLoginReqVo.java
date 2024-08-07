package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.validation.Mobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @description: 手机密码登录
 * @author: Lvhl
 * @date: 2024/8/7 13:37
 */
@Data
public class AuthMobileLoginReqVo {
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @NotEmpty(message = "密码不能为空")
    private String password;
}
