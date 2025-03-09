package com.lovbe.icharge.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/8 13:30
 * @Description: MS
 */
@Data
@Accessors(chain = true)
public class MessageActionVo {
    /**
     * 消息id
     */
    private Long messageId;
    /**
     * 动作结果
     */
    private boolean result;
    /**
     * 失败原因
     */
    private String reason;
}
