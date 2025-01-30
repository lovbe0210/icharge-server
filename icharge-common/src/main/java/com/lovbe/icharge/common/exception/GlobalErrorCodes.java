package com.lovbe.icharge.common.exception;

/**
 * 全局错误码枚举
 * 0-999 系统异常编码保留
 *
 * 一般情况下，使用 HTTP 响应状态码 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status
 * 虽然说，HTTP 响应状态码作为业务使用表达能力偏弱，但是使用在系统层面还是非常不错的
 * 比较特殊的是，因为之前一直使用 0 作为成功，就不使用 200 啦。
 *
 * @author 芋道源码
 */
public interface GlobalErrorCodes {

    ErrorCode SUCCESS = new ErrorCode(200, "成功");

    // ========== 客户端错误段 ==========

    ErrorCode BAD_REQUEST = new ErrorCode(400, "error request");
    ErrorCode LOCKED = new ErrorCode(400, "请求失败，请稍后再试");
    ErrorCode TOO_MANY_REQUESTS = new ErrorCode(400, "请求过于频繁，请稍后再试");
    ErrorCode SLIDER_VERIFY_FAILED = new ErrorCode(400, "请先完成滑块验证");
    ErrorCode UNAUTHORIZED = new ErrorCode(401, "token已过期");
    ErrorCode ACCOUNT_LOGIN_EXPIRED = new ErrorCode(403, "账号信息已过期, 请重新登录");
    ErrorCode NOT_FOUND = new ErrorCode(404, "请求未找到");

    // ========== 服务端错误段 ==========

    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统异常,请稍后再试");
    ErrorCode NOT_IMPLEMENTED = new ErrorCode(501, "功能未实现/未开启");
    ErrorCode ERROR_CONFIGURATION = new ErrorCode(502, "错误的配置项");

    // ========== 自定义错误段 ==========
    ErrorCode REPEATED_REQUESTS = new ErrorCode(900, "重复请求，请稍后再试");
    ErrorCode DEMO_DENY = new ErrorCode(901, "演示模式，禁止写操作");

    ErrorCode UNKNOWN = new ErrorCode(999, "未知错误");

}
