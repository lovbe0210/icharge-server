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
public class AuthMobilePwdLoginReqVo {
    @NotEmpty(message = "手机号或密码错误")
    private String mobile;

    @NotEmpty(message = "手机号或密码错误")
    private String password;
}
