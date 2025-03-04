package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Objects;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/23 16:21
 * @Description: 聊天会话
 */
@Data
@Accessors(chain = true)
@TableName("s_chat_logs")
public class ChatMessageLogDo extends BaseEntity {
    /**
     * 客户端Id
     */
    private String clientMsgId;
    /**
     * 发送者id 如果是系统提醒则为0
     */
    private Long sendId;
    /**
     * 接收者id
     */
    private Long recvId;
    /**
     * 接收人类型1私聊 2群聊
     */
    private Integer recvType;
    /**
     * 发送者平台 1web 2h5 3app
     */
    private Integer senderPlatformId;
    /**
     * 101 文字消息 102图片 103站内文章 104链接 111撤回消息
     */
    private Integer contentType;
    /**
     * 消息体，有前端定义前端解析
     */
    private Object content;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 阅读状态
     */
    private Integer readStatus;
}
