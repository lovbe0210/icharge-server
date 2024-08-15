package com.lovbe.icharge.util;

import com.lovbe.icharge.enums.CodeSceneEnum;

/**
 * redis键相关
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/15 21:17
 */
public abstract class RedisKeyConstant {

    /**
     * 项目前缀
     */
    public static final String BASE_PROJECT = "icharge:";
    /**
     * 权限认证服务
     */
    public static final String AUTH = "auth:";


    /**
     * @description 获取验证码
     * @param[1] scene
     * @param[2] examId
     * @param[3] type
     * @return String
     * @author lovbe0210
     * @date 2024/8/15 21:20
     */
    public static String getVerifyCode(CodeSceneEnum scene, String mobile, String email) {
        String sub = CodeSceneEnum.sceneIsMobile(scene) ? "mobile:" + mobile : "email:" + email;
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
    public static String getVerifyCount(CodeSceneEnum scene, String mobile, String email) {
        String sub = CodeSceneEnum.sceneIsMobile(scene) ? mobile : email;
        return BASE_PROJECT + AUTH + "verify-count:" + sub;
    }


    public static String getAccessTokenKey(String accessToken) {
        return BASE_PROJECT + AUTH + "access-token:" + accessToken;
    }

    public static String getRefreshTokenKey(String refreshToken) {
        return BASE_PROJECT + AUTH + "refresh-token:" + refreshToken;
    }
}
