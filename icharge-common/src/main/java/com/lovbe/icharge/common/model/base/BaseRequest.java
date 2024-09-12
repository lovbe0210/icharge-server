package com.lovbe.icharge.common.model.base;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * @description: 入参基类
 * @author: Lvhl
 * @date: 2024/7/9 11:35
 */
@NoArgsConstructor
@Accessors(chain = true)
@Data
public class BaseRequest<T> {
    private String userId;
    private String version;

    @Valid
    @NotNull(message = "参数不得为空")
    private T data;

    public BaseRequest(T data) {
        this.data = data;
    }
}
