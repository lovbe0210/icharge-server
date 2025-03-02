package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/23 16:21
 * @Description: 聊天会话
 */
@Data
@TableName("s_conversation")
@Accessors(chain = true)
public class ConversationDo extends BaseEntity {
    /**
     * 所属用户id
     */
    private Long ownerUserId;
    /**
     * 会话用户id
     */
    private Long targetUserId;
    /**
     * 是否单向会话会话 0否1是 对方主动回复或关注你前，最多发送1条消息
     */
    private int isOneWay;
    /**
     * 是否开启免打扰 0否1是
     */
    private int isNotDisturb;
    /**
     * 是否置顶 0否1是
     */
    private int isPinned;
    /**
     * 是否屏蔽会话用户 0否1是
     */
    private int isShield;
    /**
     * 未读数
     */
    private int unreadCount;
    /**
     * 最后一条消息id
     */
    private Long lastMsgId;
    /**
     * 未读统计时间
     */
    private Date updateUnreadCountTime;
    /**
     * 最小聊天记录序列号
     */
    private Long minChatLogSeq;
}
