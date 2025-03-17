package com.lovbe.icharge.common.util.redis;

import com.lovbe.icharge.common.enums.CodeSceneEnum;

/**
 * redis键相关
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/15 21:17
 */
public abstract class RedisKeyConstant {
    /**
     * 过期时间，单位都为秒
     */
    public static final long EXPIRE_10_MIN = 60 * 10;
    public static final long EXPIRE_30_MIN = 60 * 30;
    public static final long EXPIRE_1_HOUR = 60 * 60;
    public static final long EXPIRE_2_HOUR = 60 * 60 * 2;
    public static final long EXPIRE_3_HOUR = 60 * 60 * 3;
    public static final long EXPIRE_1_DAY = EXPIRE_1_HOUR * 24;
    public static final long EXPIRE_30_DAY = EXPIRE_1_HOUR * 24 * 30;
    public static final long EXPIRE_7_DAY = EXPIRE_1_HOUR * 24 * 7;

    /**
     * 项目前缀
     */
    public static final String BASE_PROJECT = "icharge:";
    /**
     * 权限认证服务
     */
    public static final String AUTH = "auth:";
    /**
     * 用户服务
     */
    public static final String USER = "user:";
    /**
     * 社交模块
     */
    public static final String SOCIAL = "social:";
    /**
     * 内容生产服务
     */
    public static final String CONTENT = "content-product:";
    /**
     * 内容精选服务
     */
    public static final String PICKS = "content-picks:";
    /**
     * 存储服务限制频率
     */
    public static final String STORAGE = "storage:";


    /**
     * @description 获取验证码
     * @param[1] scene
     * @param[2] examId
     * @param[3] type
     * @return String
     * @author lovbe0210
     * @date 2024/8/15 21:20
     */
    public static String getVerifyCode(Integer scene, String mobile, String email) {
        String sub = CodeSceneEnum.sceneIsMobile(scene) ? ("mobile:" + mobile) : ("email:" + email);
        return BASE_PROJECT + AUTH + sub;
    }

    /**
     * @description 发送验证码的时间标记
     * @param[1] scene
     * @param[2] mobile
     * @param[3] email
     * @return String
     * @author lovbe0210
     * @date 2024/8/15 21:42
     */
    public static String getSendCodeFlag(CodeSceneEnum scene, String mobile, String email) {
        String sub = CodeSceneEnum.sceneIsMobile(scene) ? mobile : email;
        return BASE_PROJECT + AUTH + "send-code:" + sub;
    }

    /**
     * @description 获取验证码验证次数
     * @param[1] scene
     * @param[2] mobile
     * @param[3] email
     * @return String
     * @author lovbe0210
     * @date 2024/8/15 21:43
     */
    public static String getVerifyCountKey(String payload) {
        return BASE_PROJECT + AUTH + "verify-count:" + payload;
    }


    /** 
     * @description: 获取accessToken
     * @param: String
     * @return: String
     * @author: lovbe0210
     * @date: 2024/8/18 14:22
     */
    public static String getAccessTokenKey(String accessToken) {
        return BASE_PROJECT + AUTH + "access-token:" + accessToken;
    }

    /** 
     * @description: 获取refreshToken
     * @param: String
     * @return: String
     * @author: lovbe0210
     * @date: 2024/8/18 14:22
     */
    public static String getRefreshTokenKey(String refreshToken) {
        return BASE_PROJECT + AUTH + "refresh-token:" + refreshToken;
    }

    /** 
     * @description: 获取验证码是否使用
     * @param: String
     * @return: String
     * @author: lovbe0210
     * @date: 2024/8/18 14:23
     */
    public static String getCodeControlKey(String payload) {
        return BASE_PROJECT + AUTH + "code-control:" + payload;
    }

    /**
     * @description: 获取验证码发送频率验证
     * @param: String
     * @return: String
     * @author: lovbe0210
     * @date: 2024/8/18 14:23
     */
    public static String getCodeLockKey(String payload) {
        return BASE_PROJECT + AUTH + "code-lock:" + payload;
    }

    /**
     * @description: 获取文件上传频率验证
     * @param: String
     * @return: String
     * @author: lovbe0210
     * @date: 2024/8/18 14:23
     */
    public static String getUploadLockKey(String scene, String payload) {
        return BASE_PROJECT + STORAGE + "upload-lock:" + scene + ":" + payload;
    }

    /**
     * @description: 获取滑块验证码
     * @param: uniqueId
     * @return: svToken
     * @author: lovbe0210
     * @date: 2024/8/18 14:25
     */
    public static String getSvTokenKey(String uniqueId, String svScene, String svToken) {
        return BASE_PROJECT + AUTH + "slider-verify:svToken:" + uniqueId + ":" + svScene + ":" + svToken;
    }

    /**
     * @description 获取展示用id集合key
     * @param[1] userId
     * @return String
     * @author lovbe0210
     * @date 2024/11/17 17:28
     */
    public static String getBeautifulKey() {
        return BASE_PROJECT + CONTENT + "beautiful-id";
    }

    /**
     * @description 获取Domain集合key
     * @param[1] userId
     * @return String
     * @author lovbe0210
     * @date 2024/11/17 17:28
     */
    public static String getDomainKey() {
        return BASE_PROJECT + USER + "domain";
    }

    /**
     * @description: 获取用户点赞列表
     * @param: Long
     * @return: String
     * @author: lovbe0210
     * @date: 2024/12/8 12:19
     */
    public static String getUserLikesSet(Long userId) {
        return BASE_PROJECT + SOCIAL + "user-like-targets:" + userId;
    }

    /**
     * @description: 获取文章专栏随笔的点赞列表
     * @param: Long
     * @return: String
     * @author: lovbe0210
     * @date: 2024/12/8 12:19
     */
    public static String getTargetLikedSet(Long targetId) {
        return BASE_PROJECT + SOCIAL + "target-liked-users:" + targetId;
    }

    /**
     * 获取点赞变化的user和target用于刷新限量明细
     * @return
     */
    public static String getLikeChangeUserSet() {
        return BASE_PROJECT + SOCIAL + "change-user";
    }
    public static String getLikeChangeTargetSet() {
        return BASE_PROJECT + SOCIAL + "change-target";
    }

    /**
     * 获取用户缓存键
     * @param userId
     * @return
     */
    public static String getCacheUserKey(Long userId) {
        return BASE_PROJECT + USER + userId;
    }

    /**
     * 获取排行榜set
     * @param targetType
     * @return
     */
    public static String getRankSetKey(Integer targetType) {
        return BASE_PROJECT + PICKS + "rank:" + targetType;
    }

    /**
     * 获取未登录用户的阅读记录
     *
     * @param uniqueId
     * @param targetId
     * @return
     */
    public static String getViewReportFrequencyLimit(String uniqueId, Long targetId) {
        return BASE_PROJECT + PICKS + "un-login-view:" + uniqueId + ":" + targetId;
    }

    /**
     * 获取未登录用户ip限制记录
     * @param ipaddress
     * @param targetId
     * @return
     */
    public static String getViewReportIpLimit(String ipaddress, Long targetId) {
        return BASE_PROJECT + PICKS + "ip-limit-view:" + ipaddress + ":" + targetId;
    }

    /**
     * 获取上一次计算人物画像标签的时间
     * @return
     */
    public static String getLastPortraitTagTimeKey() {
        return BASE_PROJECT + PICKS + "last-portrait-time";
    }

    /**
     * 用户每日奖励
     * @param userId
     * @return
     */
    public static String getUserdailyEncourage(Long userId) {
        return BASE_PROJECT + USER + "daily-encourage:" + userId;
    }
}
