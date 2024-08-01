package com.lovbe.icharge.storage.exception;

import com.lovbe.icharge.common.exception.ServiceException;

/**
 * @description: 文件存储相关异常
 * @author: Lvhl
 * @date: 2024/5/9 13:09
 */
public class StorageException extends ServiceException {
    public StorageException(String msg) {
        super(msg);
    }

    public StorageException(String msg, Throwable e) {
        super(msg, e);
    }
}
