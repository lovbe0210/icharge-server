package com.lovbe.icharge.common.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件上传枚举
 *
 * @author lovbe0210
 */
@Getter
@AllArgsConstructor
public enum UploadSceneEnum implements BaseEnum {

    ARTICLE_FILE("article", "文章/随笔/个人主页内文件"),
    USER_AVATAR("avatar", "用户头像"),
    BACKGROUND_IMAGE("background", "自定义背景图片"),
    CHAT_FILE("chat", "聊天图片"),
    COMMENT_IMAGE("comment", "评论回复图片"),
    COVER_IMAGE("cover", "文章专栏封面图");

    /**
     * 上传场景
     */
    private final String scene;
    /**
     * 描述
     */
    private final String description;


    public static UploadSceneEnum getCodeByScene(String scene) {
        return ArrayUtil.firstMatch(sceneEnum -> sceneEnum.getScene() == scene, values());
    }

    @Override
    public Object getCode() {
        return this.scene;
    }
}
