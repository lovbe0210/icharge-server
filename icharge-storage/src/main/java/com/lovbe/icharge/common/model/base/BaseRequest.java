package com.lovbe.icharge.common.model.base;


import lombok.Data;

/**
 * @description: 入参基类
 * @author: Lvhl
 * @date: 2024/7/9 11:35
 */
@Data
public class BaseRequest<T> {
    private String userId;
    private T data;
}
