package com.lovbe.icharge.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/18 23:25
 * @Description: 电池激励类型
 */
@Getter
@AllArgsConstructor
public enum EncorageBehaviorEnum {
    BEHAVIOR_PUBLISH(1, 3, "发表 1 篇文档"),
    BEHAVIOR_LIKED(2, 2, "内容获得 1 个点赞"),
    BEHAVIOR_FEATURE(3, 5,"文档获得 1 次精选"),
    BEHAVIOR_COMMENT(4, 1,"内容获得 1 条评论"),
    BEHAVIOR_NEW_FAN(5, 3,"新增 1 个粉丝");

    /**
     * 激励类型
     */
    private final int behaviorType;
    /**
     * 激励电池
     */
    private final int encourageScore;
    /**
     * 描述
     */
    private final String desc;

    public static EncorageBehaviorEnum getEncourageEnum(int behaviorType) {
        EncorageBehaviorEnum[] values = EncorageBehaviorEnum.values();
        for (EncorageBehaviorEnum value : values) {
            if (value.getBehaviorType() == behaviorType) {
                return value;
            }
        }
        return null;
    }
}
