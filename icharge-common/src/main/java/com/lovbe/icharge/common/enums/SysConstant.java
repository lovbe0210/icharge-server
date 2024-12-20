package com.lovbe.icharge.common.enums;

/**
 * @Author: lovbe
 * @Date: 2024/8/13 22:29
 * @Description: 系统常量
 */
public class SysConstant {
    public static String NOT_EMPTY_MOBILE = "手机号不得为空";
    public static String NOT_QUALIFIED_MOBILE = "手机号格式不正确";
    public static String NOT_EMPTY_EMAIL = "邮箱不得为空";
    public static String NOT_QUALIFIED_EMAIL = "邮箱格式不正确";

    public static final String MOBILE = "mobile";
    public static final String EMAIL = "email";
    public static final String CODE = "code";
    public static final String SUCCESS = "success";
    public static final String FAILED = "failed";

    public static final String TIMESTAMP = "timestamp";
    public static final String UNIQUE_ID = "unique_id";
    public static final String SV_SCENE = "sv_scene";
    public static final String ACCESS_TOKEN = "icharge_actoken";
    public static final String REFRESH_TOKEN = "icharge_rftoken";
    public static final String LOGIN_USER_ID = "x-login-id";
    public static final String ID = "id";
    public static final String TN = "tn";
    public static final String USERID = "userId";

    public static final String FILE_SCENE_AVATAR = "avatar";
    public static final String FILE_SCENE_COVER = "cover";
    public static final String FILE_SCENE_COMMENT = "comment";
    public static final String UPDATE_TIME = "updateTime";
    public static final String CONTENT_ID = "contentId";
    public static final String TOTAL = "total";
    public static final String LIST = "list";

    public static final String UNKNOWN_USER_NAME = "未知用户";
    public static final String DISABLE_USER_NAME = "封禁用户";
    public static final String LOGOFF_USER_NAME = "注销用户";

    /**
     * 文章批量发布
     */
    public static final String ARTICLE_BATCH_PUBLISH = "1";
    /**
     * 文章批量移动出专栏
     */
    public static final String ARTICLE_BATCH_REMOVE = "2";
    /**
     * 文章批量移至专栏
     */
    public static final String ARTICLE_BATCH_MOVE = "3";
    /**
     * 文章批量导出
     */
    public static final String ARTICLE_BATCH_EXPORT = "4";
    /**
     * 文章批量删除
     */
    public static final String ARTICLE_BATCH_DELETE = "5";

    public static final long SIZE_10MB = 10 * 1024 * 1024;
    /**
     * 缓存时间 30天，单位秒
     */
    public static final long DAY_30 = 60 * 60 * 24 * 30;

    public static final String NORMAL_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
