package com.lovbe.icharge.common.exception;

/**
 * @description: 认证异常
 * @author: Lvhl
 * @date: 2024/5/9 13:12
 */
public class AuthorizationException extends ServiceException {
    public AuthorizationException(String msg) {
        super(msg);
    }
}
