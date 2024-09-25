package com.lovbe.icharge.dto.vo;

import com.lovbe.icharge.common.validation.Mobile;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @description: 手机密码登录
 * @author: Lvhl
 * @date: 2024/8/7 13:37
 */
@Data
public class AuthMobileLoginReqVo {
    @NotBlank(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @NotBlank(message = "用户名或密码错误")
    private String password;
}
