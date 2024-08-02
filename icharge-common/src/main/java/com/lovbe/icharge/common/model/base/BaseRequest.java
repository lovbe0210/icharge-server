package com.lovbe.icharge.common.model.base;


import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @description: 入参基类
 * @author: Lvhl
 * @date: 2024/7/9 11:35
 */
@Data
public class BaseRequest<T> {
    private String userId;
    private String version;
    @NotEmpty(message = "参数不得为空")
    private T data;
}
