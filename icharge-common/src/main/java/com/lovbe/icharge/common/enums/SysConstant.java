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
    public static final String FILE_SCENE_CHAT = "chat";
    public static final String FILE_SCENE_BACKGROUND = "background";
    public static final String FILE_SCENE_content = "content";
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
     * 人际关系：
     */
    public static final String RELATIONSHIP_FOLLOW = "follow";
    public static final String RELATIONSHIP_FANS = "fans";

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

    public static final long SIZE_100MB = 100 * 1024 * 1024;
    public static final long SIZE_50MB = 50 * 1024 * 1024;
    public static final long SIZE_10MB = 10 * 1024 * 1024;
    public static final long SIZE_5MB = 5 * 1024 * 1024;
    /**
     * 缓存时间，单位秒
     */
    public static final long DAY_30 = 60 * 60 * 24 * 30;
    public static final long DAY_1 = 60 * 60 * 24;
    public static final long HOUR_1 = 60 * 60;

    public static final String NORMAL_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 内容对象类型 1文章 2专栏 3随笔 4评论
     * 0 创作者，用户
     */
    public static final int TARGET_TYPE_AUTHOR = 0;
    public static final int TARGET_TYPE_ARTICLE = 1;
    public static final int TARGET_TYPE_COLUMN = 2;
    public static final int TARGET_TYPE_ESSAY = 3;
    public static final int TARGET_TYPE_COMMENT = 4;

    /**
     * 0未发布 1审核中 2审核失败 3已发布
     */
    public static final int PUBLISH_WAIT = 0;
    public static final int PUBLISH_AUDIT = 1;
    public static final int PUBLISH_FAILED = 2;
    public static final int PUBLISH_SUCCESS = 3;

    /**
     * 通知类型 1评论 2回复 3点赞 4新增关注 0系统消息
     */
    public static final int NOTICE_COMMENT = 1;
    public static final int NOTICE_REPLY = 2;
    public static final int NOTICE_LIKE = 3;
    public static final int NOTICE_FOLLOW = 4;
    public static final int NOTICE_AUDIT_ARTICLE = 5;
    public static final int NOTICE_AUDIT_EASSAY = 6;
    public static final int NOTICE_SYSTEM = 0;

    /**
     * 消息提醒人员范围 1 所有人 0关注的人 -1不接受任何消息提醒
     */
    public static final int NOTICE_FOLLOW_USER = 0;
    public static final int NOTICE_ALL_USER = 1;
    public static final int NOTICE_NONE_USER = -1;

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
    public static final String ES_INDEX_COLUMN = "c_column";
    public static final String ES_INDEX_USER = "p_user";

    /**
     * es字段类型
     */
    public static final String ES_FIELD_TYPE_LONG = "long";
    public static final String ES_FIELD_TYPE_INTEGER = "integer";
    public static final String ES_FIELD_TYPE_TEXT = "text";

    /**
     * es索引字段名
     */
    // AI总结的文章标签
    public static final String ES_FILED_TAG_AI = "tags";
    public static final String ES_FILED_TAG_USER = "userTags";
    // AI总结的文章分类
    public static final String ES_FILED_CATEGORY = "category";
    // 用户设置的一级分类
    public static final String ES_FILED_CATEGORY1 = "firstCategory";
    // 用户设置的二级分类
    public static final String ES_FILED_CATEGORY2 = "secondCategory";
    // 是否公开
    public static final String ES_FILED_PUBLIC = "isPublic";
    // 文章、专栏、用户的uid
    public static final String ES_FILED_UID = "uid";
    // 文章、专栏标题
    public static final String ES_FILED_TITLE = "title";
    // 文章摘要
    public static final String ES_FILED_SUMMARY = "summary";
    // 专栏简介
    public static final String ES_FILED_SYNOPSIS = "synopsis";
    // 文章内容
    public static final String ES_FILED_CONTENT = "content";
    // 用户名
    public static final String ES_FILED_USERNAME = "username";

    /**
     * 标签json字段-内容
     */
    public static final String TAG_FIELD_CONTENT = "content";
    public static final String SESSION_ID = "sessionId";
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";

    /**
     * ws消息中前端回调方法
     */
    public static final String GET_SESSION_LIST = "getSessionList";
    public static final String SEND_MESSAGE = "sendMessage";
    public static final String DELETE_MESSAGE = "deleteMessage";
    public static final String ROLLBACK_MESSAGE = "rollbackMessage";
    public static final String MESSAGE_CONFIRM = "messageConfirm";
    public static final String RECV_MESSAGE = "recvMessage";
    public static final String GET_CHAT_LOGS = "getChatLogs";
    public static final String GET_UNREAD_COUNT = "getUnreadCount";

    /**
     * 聊天消息类型
     */
    public static final int MESSAGE_ROLLBACK = 111;

    /**
     * 消息类型 0心跳 1会话相关 2消息相关
     */
    public static final int MSG_TYPE_HEATER_BEAR = 0;
    public static final int MSG_TYPE_SESSION = 1;
    public static final int MSG_TYPE_MESSAGE = 2;


}
