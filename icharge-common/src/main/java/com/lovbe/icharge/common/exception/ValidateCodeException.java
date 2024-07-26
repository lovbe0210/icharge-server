package com.lovbe.icharge.common.exception;

/**
 * @description: 验证码错误
 * @author: Lvhl
 * @date: 2024/5/9 13:11
 */
public class ValidateCodeException extends ServiceException {
    public ValidateCodeException(String msg) {
        super(msg);
    }
}
