package com.lovbe.icharge.common.model.base;

import com.lovbe.icharge.common.exception.ErrorCode;
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

    public ResponseBean(ErrorCode errorCode) {
        this.result = false;
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
        this.data = null;
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

    public static <T> ResponseBean error(int code, String message, T t) {
        return new ResponseBean(code, false, message, t);
    }

    public static ResponseBean error(ErrorCode errorCode, String data) {
        return new ResponseBean(errorCode.getCode(), false, errorCode.getMsg(), data);
    }
}
