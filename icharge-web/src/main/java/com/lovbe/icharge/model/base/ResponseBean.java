package com.lovbe.icharge.model.base;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseBean<T> {
    private int code;
    private boolean result;
    private String message;
    private T data;
    private String version = "V1.0";

    public ResponseBean(int code, boolean result, String message) {
        this.code = code;
        this.result = result;
        this.message = message;
    }

    public ResponseBean(int code, boolean result, String message, T data) {
        this.code = code;
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public static ResponseBean ok() {
        return new ResponseBean(200, true, "操作成功", null);
    }

    public static  <T> ResponseBean ok(T t) {
        return new ResponseBean(200, true, "操作成功", t);
    }

    public static ResponseBean error(int code, String message) {
        return new ResponseBean(code, false, message);
    }

    public static ResponseBean error(RequestResultEnum resultEnum) {
        return new ResponseBean(resultEnum.getCode(), false, resultEnum.getMsg());
    }
}
