package com.lovbe.icharge.dto.dto;

import lombok.Data;

/**
 * @description: 权限认证相关
 * @author: Lvhl
 * @date: 2024/8/2 15:26
 */
@Data
public class AuthInfoDTO {
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 密码
     */
    private String password;
    /**
     * 验证码
     */
    private String verifyCode;

}
