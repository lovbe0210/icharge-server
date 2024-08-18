package com.lovbe.icharge.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录结果的枚举类
 */
@Getter
@AllArgsConstructor
public enum LoginResultEnum {

    SUCCESS(0), // 成功
    BAD_CREDENTIALS(10), // 账号或密码不正确
    USER_DISABLED(20), // 用户被禁用
    SLIDER_VERIFY_FAILED(30), // 滑块验证失败
    CAPTCHA_CODE_ERROR(31), // 验证码不正确

    ;

    /**
     * 结果
     */
    private final Integer result;

}
