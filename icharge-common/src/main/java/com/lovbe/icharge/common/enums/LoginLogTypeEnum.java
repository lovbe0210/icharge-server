package com.lovbe.icharge.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 登录日志的类型枚举
 * @author: Lvhl
 * @date: 2024/8/7 16:53
 */
@Getter
@AllArgsConstructor
public enum LoginLogTypeEnum implements BaseEnum {

    LOGIN_MOBILE_PASSWORD(101, "使用手机密码登录"),
    LOGIN_EMAIL_PASSWORD(102, "使用邮箱密码登录"),
    LOGIN_SMS_CODE(103, "使用短信验证码登陆"),
    LOGIN_EMAIL_CODE(104, "使用邮箱验证码登陆"),
    LOGIN_SOCIAL(105, "使用社交登录"),

    LOGOUT_SELF(400, "自己主动登出"),
    LOGOUT_DELETE(403, "强制退出"),
    ;

    /**
     * 日志类型
     */
    private final Integer type;
    private final String desc;

    @Override
    public Object getCode() {
        return type;
    }
}
