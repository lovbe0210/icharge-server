package com.lovbe.icharge.common.exception;

/**
 * Member 错误码枚举类
 * <p>
 * member 系统，使用 1-004-000-000 段
 */
public interface ServiceErrorCodes {

    // ========== 用户相关  1003 ============
    ErrorCode USER_MOBILE_NOT_EXISTS = new ErrorCode(100301, "手机号未注册用户，请注册后再进行登录");
    ErrorCode USER_EMAIL_NOT_EXISTS = new ErrorCode(100302, "邮箱未注册用户，请注册后再进行登录");
    ErrorCode USER_MOBILE_USED = new ErrorCode(100303, "修改手机失败，该手机号({})已经被使用");
    ErrorCode USER_POINT_NOT_ENOUGH = new ErrorCode(100304, "用户积分余额不足");
    ErrorCode MOBILE_NOT_EXIST = new ErrorCode(100305, "手机号不存在");
    ErrorCode SIMPLE_CODE_SEND_FAILED = new ErrorCode(100306, "验证码发送失败，请稍后再试");

    // ========== AUTH 模块 1004 ==========
    ErrorCode AUTH_LOGIN_BAD_CREDENTIALS = new ErrorCode(100401, "登录失败，账号密码不正确");
    ErrorCode AUTH_LOGIN_USER_DISABLED = new ErrorCode(100402, "登录失败，账号被禁用");
    ErrorCode AUTH_SOCIAL_USER_NOT_FOUND = new ErrorCode(100403, "登录失败，解析不到三方登录信息");
    ErrorCode AUTH_MOBILE_USED = new ErrorCode(100404, "手机号已经被使用");
    ErrorCode AUTH_CODE_EXPIRED = new ErrorCode(100405, "验证码已过期，请重新获取");
    ErrorCode AUTH_CODE_ERROR = new ErrorCode(100406, "验证码错误");
    ErrorCode AUTH_EMPTY_LOGIN_TYPE = new ErrorCode(100407, "登录方式不得为空");
    ErrorCode AUTH_FAILED = new ErrorCode(100408, "登录失败，请稍后再试！");
    ErrorCode AUTH_ACCOUNT_STATUS_ERROR = new ErrorCode(100309, "账号状态异常，请刷新页面重试");

    // ========== feign 远程调用相关 ==========
    ErrorCode FEIGN_FAILED_REQUEST = new ErrorCode(1005, "Feign接口请求失败");

    //========== 用户标签 1-004-006-000 ==========
    ErrorCode TAG_NOT_EXISTS = new ErrorCode(1_004_006_000, "用户标签不存在");
    ErrorCode TAG_NAME_EXISTS = new ErrorCode(1_004_006_001, "用户标签已经存在");
    ErrorCode TAG_HAS_USER = new ErrorCode(1_004_006_002, "用户标签下存在用户，无法删除");

    //========== 积分配置 1-004-007-000 ==========

    //========== 积分记录 1-004-008-000 ==========
    ErrorCode POINT_RECORD_BIZ_NOT_SUPPORT = new ErrorCode(1_004_008_000, "用户积分记录业务类型不支持");

    //========== 签到配置 1-004-009-000 ==========
    ErrorCode SIGN_IN_CONFIG_NOT_EXISTS = new ErrorCode(1_004_009_000, "签到天数规则不存在");
    ErrorCode SIGN_IN_CONFIG_EXISTS = new ErrorCode(1_004_009_001, "签到天数规则已存在");

    //========== 签到配置 1-004-010-000 ==========
    ErrorCode SIGN_IN_RECORD_TODAY_EXISTS = new ErrorCode(1_004_010_000, "今日已签到，请勿重复签到");

    //========== 用户等级 1-004-011-000 ==========
    ErrorCode LEVEL_NOT_EXISTS = new ErrorCode(1_004_011_000, "用户等级不存在");
    ErrorCode LEVEL_NAME_EXISTS = new ErrorCode(1_004_011_001, "用户等级名称[{}]已被使用");
    ErrorCode LEVEL_VALUE_EXISTS = new ErrorCode(1_004_011_002, "用户等级值[{}]已被[{}]使用");
    ErrorCode LEVEL_EXPERIENCE_MIN = new ErrorCode(1_004_011_003, "升级经验必须大于上一个等级[{}]设置的升级经验[{}]");
    ErrorCode LEVEL_EXPERIENCE_MAX = new ErrorCode(1_004_011_004, "升级经验必须小于下一个等级[{}]设置的升级经验[{}]");
    ErrorCode LEVEL_HAS_USER = new ErrorCode(1_004_011_005, "用户等级下存在用户，无法删除");

    ErrorCode EXPERIENCE_BIZ_NOT_SUPPORT = new ErrorCode(1_004_011_201, "用户经验业务类型不支持");

    //========== 用户分组 1-004-012-000 ==========
    ErrorCode GROUP_NOT_EXISTS = new ErrorCode(1_004_012_000, "用户分组不存在");
    ErrorCode GROUP_HAS_USER = new ErrorCode(1_004_012_001, "用户分组下存在用户，无法删除");

}
