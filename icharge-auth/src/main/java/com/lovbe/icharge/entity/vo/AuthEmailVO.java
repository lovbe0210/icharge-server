package com.lovbe.icharge.entity.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * @description: 邮箱相关请求体
 * @author: Lvhl
 * @date: 2024/8/7 13:10
 */
@Data
public class AuthEmailVO {
    /**
     * 手机号
     */
    @Email
    @NotEmpty(message = "邮箱不能为空")
    private String mobile;
}
