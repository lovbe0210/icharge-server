package com.lovbe.icharge.common.exception;

/**
 * @description: 业务异常
 * @author: Lvhl
 * @date: 2024/5/9 13:11
 */
public class ServiceException extends BaseException {
    private static final long serialVersionUID = 1L;
    private final int code = 501;
    private String msg;


    public ServiceException(String msg) {
        super(msg);
    }
}
