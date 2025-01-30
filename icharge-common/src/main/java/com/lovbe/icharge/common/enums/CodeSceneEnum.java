package com.lovbe.icharge.common.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户验证码场景的枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum CodeSceneEnum implements BaseEnum {

    MOBILE_LOGIN(1, "mobile-login", "手机号登陆"),
    EMAIL_LOGIN(2, "email-login", "邮箱登陆"),
    BIND_MOBILE(3, "bind-mobile", "绑定手机号"),
    VERIFY_MOBILE(31, "verify-mobile", "验证新手机号"),
    BIND_EMAIL(4, "bing-email", "绑定邮箱"),
    VERIFY_EMAIL(41, "verify-email", "验证新邮箱号"),
    MOBILE_UPDATE_MOBILE(5, "update-mobile-by-mobile", "修改手机号码（手机号验证）"),
    EMAIL_UPDATE_MOBILE(51, "update-mobile-by-email", "修改手机号码（邮箱号验证）"),
    EMAIL_UPDATE_EMAIL(6, "update-email-by-email", "修改邮箱（邮箱号验证）"),
    MOBILE_UPDATE_EMAIL(61, "update-email-by-mobile", "修改邮箱（手机号验证）"),
    MOBILE_UPDATE_PASSWORD(7, "update-password-by-mobile", "使用手机设置/修改密码"),
    EMAIL_UPDATE_PASSWORD(8, "update-password-by-email", "使用邮箱设置/修改密码"),
    MOBILE_RESET_PASSWORD(9, "reset-password-by-mobile", "忘记密码/手机重置"),
    EMAIL_RESET_PASSWORD(10, "reset-password-by-email", "忘记密码/邮箱重置");

    /**
     * 验证场景的编号
     */
    private final int scene;
    /**
     * 模版编码
     */
    private final String templateCode;
    /**
     * 描述
     */
    private final String description;


    public static CodeSceneEnum getCodeByScene(int scene) {
        return ArrayUtil.firstMatch(sceneEnum -> sceneEnum.getScene() == scene, values());
    }

    public static boolean sceneIsMobile(CodeSceneEnum scene) {
        return MOBILE_LOGIN.equals(scene) ? true
                : BIND_EMAIL.equals(scene) ? true
                : VERIFY_MOBILE.equals(scene) ? true
                : MOBILE_UPDATE_MOBILE.equals(scene) ? true
                : MOBILE_UPDATE_EMAIL.equals(scene) ? true
                : MOBILE_UPDATE_PASSWORD.equals(scene) ? true
                : MOBILE_RESET_PASSWORD.equals(scene);
    }

    public static boolean sceneIsMobile(Integer sceneCode) {
        return MOBILE_LOGIN.getCode().equals(sceneCode) ? true
                : BIND_EMAIL.getCode().equals(sceneCode) ? true
                : VERIFY_MOBILE.getCode().equals(sceneCode) ? true
                : MOBILE_UPDATE_MOBILE.getCode().equals(sceneCode) ? true
                : MOBILE_UPDATE_EMAIL.getCode().equals(sceneCode) ? true
                : MOBILE_UPDATE_PASSWORD.getCode().equals(sceneCode) ? true
                : MOBILE_RESET_PASSWORD.getCode().equals(sceneCode);
    }

    public static boolean sceneIsEmail(CodeSceneEnum scene) {
        return EMAIL_LOGIN.equals(scene) ? true
                : BIND_MOBILE.equals(scene) ? true
                : VERIFY_EMAIL.equals(scene) ? true
                : EMAIL_UPDATE_MOBILE.equals(scene) ? true
                : EMAIL_UPDATE_EMAIL.equals(scene) ? true
                : EMAIL_UPDATE_PASSWORD.equals(scene) ? true
                : EMAIL_RESET_PASSWORD.equals(scene);
    }

    public static boolean sceneIsEmail(Integer scene) {
        return EMAIL_LOGIN.getCode().equals(scene) ? true
                : BIND_MOBILE.getCode().equals(scene) ? true
                : VERIFY_EMAIL.getCode().equals(scene) ? true
                : EMAIL_UPDATE_MOBILE.getCode().equals(scene) ? true
                : EMAIL_UPDATE_EMAIL.getCode().equals(scene) ? true
                : EMAIL_UPDATE_PASSWORD.getCode().equals(scene) ? true
                : EMAIL_RESET_PASSWORD.getCode().equals(scene);
    }

    public static boolean sceneMustLogin(Integer sceneCode) {
        return BIND_MOBILE.getCode().equals(sceneCode) ? true
                : VERIFY_MOBILE.getCode().equals(sceneCode) ? true
                : BIND_EMAIL.getCode().equals(sceneCode) ? true
                : VERIFY_EMAIL.getCode().equals(sceneCode) ? true
                : MOBILE_UPDATE_MOBILE.getCode().equals(sceneCode) ? true
                : EMAIL_UPDATE_MOBILE.getCode().equals(sceneCode) ? true
                : EMAIL_UPDATE_EMAIL.getCode().equals(sceneCode) ? true
                : MOBILE_UPDATE_EMAIL.getCode().equals(sceneCode) ? true
                : MOBILE_UPDATE_PASSWORD.getCode().equals(sceneCode) ? true
                : EMAIL_UPDATE_PASSWORD.getCode().equals(sceneCode);
    }

    @Override
    public Object getCode() {
        return this.scene;
    }
}
