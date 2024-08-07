package com.lovbe.icharge.common.exception;

import lombok.Data;

/**
 * @description: 自定义基类
 * @author: Lvhl
 * @date: 2024/5/9 13:11
 */
@Data
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public ServiceException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

}
