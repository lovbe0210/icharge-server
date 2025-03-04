package com.lovbe.icharge.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/28 14:58
 * @Description: MS
 */
@Data
public class ChatMessageVo {
    /**
     * 服务端Id
     */
    private Long serverMsgId;
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
     * 接受者类型1私聊 2群聊
     */
    private Integer recvType;
    /**
     * 100 文字消息 101图片 102站内文章 103链接 111撤回消息 112系统消息
     */
    private Integer contentType;
    /**
     * 消息体，前端定义前端解析
     */
    private String content;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 阅读状态
     */
    private Integer readStatus;
    /**
     * 消息状态 A正常 D删除 S撤回
     */
    private String status;
    private Date createTime;
    private Date updateTime;
}
