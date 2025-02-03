package com.lovbe.icharge.common.exception;

/**
 * Member 错误码枚举类
 * <p>
 * member 系统，使用 1-004-000-000 段
 */
public interface ServiceErrorCodes {

    // ========== 用户相关 ============
    ErrorCode USER_MOBILE_NOT_EXISTS = new ErrorCode(400, "手机号未注册用户，请注册后再进行登录");
    ErrorCode USER_EMAIL_NOT_EXISTS = new ErrorCode(400, "邮箱未注册用户，请注册后再进行登录");
    ErrorCode USER_NOT_EXIST = new ErrorCode(400, "请求错误，用户信息不存在");
    ErrorCode USER_ACCOUNT_NOT_EXISTS = new ErrorCode(400, "没有找到匹配的账号");
    ErrorCode USER_MOBILE_USED = new ErrorCode(400, "手机号已经被使用");
    ErrorCode USER_EMAIL_USED = new ErrorCode(400, "邮箱号已经被使用");
    ErrorCode USER_DOMAIN_USED = new ErrorCode(400, "个人路径已经被使用");
    ErrorCode USER_POINT_NOT_ENOUGH = new ErrorCode(400, "用户积分余额不足");
    ErrorCode MOBILE_NOT_EXIST = new ErrorCode(400, "手机号不存在");
    ErrorCode SIMPLE_CODE_SEND_FAILED = new ErrorCode(400, "验证码发送失败，请稍后再试");
    ErrorCode ACCOUNT_DISABLED = new ErrorCode(405, "账号被禁用");
    ErrorCode USER_DISABLED = new ErrorCode(405, "用户被禁用");
    ErrorCode ACCOUNT_PASSWORD_RESET_FAILED = new ErrorCode(400, "密码重置失败，请稍后再试");
    ErrorCode USER_INFO_UPDATE_FAILED = new ErrorCode(400, "更新失败，请稍后再试");

    // ========== AUTH 模块 ==========
    ErrorCode AUTH_LOGIN_BAD_CREDENTIALS = new ErrorCode(400, "登录失败，账号或密码不正确");
    ErrorCode AUTH_LOGIN_USER_DISABLED = new ErrorCode(405, "登录失败，账号被禁用");
    ErrorCode AUTH_SOCIAL_USER_NOT_FOUND = new ErrorCode(400, "登录失败，解析不到三方登录信息");
    ErrorCode AUTH_CODE_EXPIRED = new ErrorCode(406, "验证码已过期，请重新获取");
    ErrorCode AUTH_CODE_ERROR = new ErrorCode(400, "验证码错误");
    ErrorCode AUTH_EMPTY_LOGIN_TYPE = new ErrorCode(400, "登录方式不得为空");
    ErrorCode AUTH_FAILED = new ErrorCode(400, "登录失败，请稍后再试！");
    ErrorCode AUTH_ACCOUNT_STATUS_ERROR = new ErrorCode(400, "账号状态异常，请刷新页面重试");

    // ========== feign 远程调用相关 ==========
    ErrorCode FEIGN_FAILED_REQUEST = new ErrorCode(1005, "Feign接口请求失败");

    //========== 公共模块 ==========
    ErrorCode FILE_UPLOAD_FAILED = new ErrorCode(400, "文件上传失败，请稍后再试");
    ErrorCode FILE_OUT_SIZE_10 = new ErrorCode(400, "文件上传失败，请选择小于10MB的文件进行上传。");
    ErrorCode FILE_PATH_ERROR = new ErrorCode(400, "文件地址保存失败，请稍后再试。");

    //========== 内容生产 ==========
    ErrorCode ARTICLE_NOT_EXIST = new ErrorCode(400, "文章不存在或已被删除");
    ErrorCode COLUMN_NOT_EXIST = new ErrorCode(400, "专栏不存在或已被删除");
    ErrorCode COLUMN_NOT_PUBLIC = new ErrorCode(400, "专栏权限为仅作者可访问");
    ErrorCode RAMBLY_JOT_NOT_EXIST = new ErrorCode(400, "随笔不存在或已被删除");
    ErrorCode ARTICLE_STATUS_ERROR = new ErrorCode(400, "文章违规封禁，不可浏览或编辑");
    ErrorCode COLUMN_STATUS_ERROR = new ErrorCode(400, "专栏违规封禁，不可浏览或编辑");
    ErrorCode ARTICLE_INFO_UPDATE_FAILED = new ErrorCode(400, "文章更新失败，请稍后再试");
    ErrorCode ARTICLE_EMPTY_PUBLISH_FAILED = new ErrorCode(400, "文章内容为空，发布失败");
    ErrorCode ARTICLE_REPEAT_PUBLISH_FAILED = new ErrorCode(400, "最新内容已发布，无需再次发布");
    ErrorCode ARTICLE_PUBLISH_FAILED = new ErrorCode(400, "请先将阅读权限设置为互联网可访问再进行发布");
    ErrorCode ARTICLE_PUBLISH_FAILED2 = new ErrorCode(400, "发布失败，请检查文章状态是否正确");

    //========== 内容推荐精选 ==========
    ErrorCode REPEAT_FAVORITE_TAG = new ErrorCode(400, "已存在相同名称的收藏分组");
    ErrorCode CATE_MENU_NOT_NULL = new ErrorCode(400, "分类菜单不得为空");

    //========== 社交排行 ==========
    ErrorCode REPLY_PARENT_ID_NOT_NULL = new ErrorCode(400, "回复评论id不得为空");
    ErrorCode COMMENT_IMAGE_UPLOAD_FAILED = new ErrorCode(400, "评论图片上传失败，请稍后再试");
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
