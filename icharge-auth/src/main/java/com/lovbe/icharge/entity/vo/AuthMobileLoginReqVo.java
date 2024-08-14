package com.lovbe.icharge.entity.vo;

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

    @NotBlank(message = "密码不能为空")
    private String password;
}
