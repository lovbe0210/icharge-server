package com.lovbe.icharge.storage.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @description: Oss类型枚举
 * @author: Lvhl
 * @date: 2024/5/9 13:01
 */
@Getter
@AllArgsConstructor
public enum OssTypeEnum {
    /**
     * 阿里云
     */
    ALIYUN(0),

    /**
     * 七牛云
     */
    QINIU(1),
    /**
     * 又拍云
     */
    UPYUN(2),

    /**
     * 本地存储
     */
    LOCAL(3),
    /**
     * 通用S3协议
     */
    S3(4);


    @JsonValue
    private final int value;

    @JsonCreator
    public static OssTypeEnum fromString(String code) {
        OssTypeEnum status = Arrays.stream(OssTypeEnum.values()).filter(item -> item.toString().equals(code)).findFirst().get();
        return status;
    }
}
