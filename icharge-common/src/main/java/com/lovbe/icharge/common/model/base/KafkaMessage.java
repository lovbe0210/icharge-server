package com.lovbe.icharge.common.model.base;

import cn.hutool.core.util.IdUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 12:08
 * @Description: kafka消息体
 */
@Data
@NoArgsConstructor
public class KafkaMessage<T> {
    /**
     * 主题
     */
    private String topic;
    /**
     * 产品代号
     */
    private String appName;
    /**
     * 消息ID（md5） snowflakeId
     */
    private String msgId;
    private Long timestamp;
    private T data;

    public KafkaMessage(String appName, String topic, T t) {
        this.data = t;
        this.topic = topic;
        this.msgId = IdUtil.getSnowflakeNextIdStr();
        this.timestamp = System.currentTimeMillis();
        this.appName = appName;
    }
}
