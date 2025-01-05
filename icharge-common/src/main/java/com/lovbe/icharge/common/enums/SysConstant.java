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
    public static final String ST = "st";
    public static final String CSH = "csh";
    public static final String SH = "sh";
    public static final String USERID = "userId";

    public static final String FILE_SCENE_AVATAR = "avatar";
    public static final String FILE_SCENE_COVER = "cover";
    public static final String FILE_SCENE_COMMENT = "comment";
    public static final String UPDATE_TIME = "updateTime";
    public static final String CONTENT_ID = "contentId";
    public static final String TARGET_ID = "targetId";
    public static final String TARGET_TYPE = "targetType";
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
     * 缓存时间，单位秒
     */
    public static final long DAY_30 = 60 * 60 * 24 * 30;
    public static final long DAY_1 = 60 * 60 * 24;

    public static final String NORMAL_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 内容对象类型 1文章 2专栏 3随笔 4评论
     */
    public static final Integer TARGET_TYPE_ARTICLE = 1;
    public static final Integer TARGET_TYPE_COLUMN = 2;
    public static final Integer TARGET_TYPE_ESSAY = 3;
    public static final Integer TARGET_TYPE_COMMENT = 4;

    /**
     * 0未发布 1审核中 2审核失败 3已发布
     */
    public static final int PUBLISH_WAIT = 0;
    public static final int PUBLISH_AUDIT = 1;
    public static final int PUBLISH_FAILED = 2;
    public static final int PUBLISH_SUCCESS = 3;

    /**
     * 分隔符
     */
    public static final String SEPARATOR = "_";
    /**
     * 正文结构类型
     */
    public static final String CHILDREN = "children";
    public static final String TEXT = "text";
    public static final String KIMI_FAILED_TYPE = "content_filter";
    public static final String KIMI_FAILED_KEYWORDS = "risk";

    /**
     * es索引名称
     */
    public static final String ES_INDEX_ARTICLE = "c_article";
    public static final String ES_INDEX_USER = "p_user";
}
