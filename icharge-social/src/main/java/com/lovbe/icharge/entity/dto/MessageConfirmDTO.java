package com.lovbe.icharge.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/4 22:32
 * @Description: 消息确认dto
 */
@Data
@Accessors(chain = true)
public class MessageConfirmDTO extends ChatMessageLogDo{
    /**
     * 会话id
     */
    private Long conversationId;
    /**
     * 发送状态是否成功 0否1是
     */
    private int sendSuccess;
    /**
     * 失败原因
     */
    private String errorReason;
}
