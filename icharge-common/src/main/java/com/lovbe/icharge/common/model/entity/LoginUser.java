package com.lovbe.icharge.common.model.entity;

import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description: 登录相关的用户信息
 * @author: Lvhl
 * @date: 2024/8/2 15:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginUser extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 密码
     */
    private String password;
}
