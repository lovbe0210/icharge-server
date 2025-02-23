package com.lovbe.icharge.entity.vo;

import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/24 0:00
 * @Description: 未读消息统计
 */
@Data
public class UnreadMsgStatisticVo {
    /**
     * 未读回复数
     */
    private int commentCount;
    /**
     * 未读点赞数
     */
    private int likeCount;
    /**
     * 未读新增关注数
     */
    private int newFollowCount;
    /**
     * 未读系统消息
     */
    private int systemMsgCount;
    /**
     * 未读私聊消息数
     */
    private int chatMsgCount;
    /**
     * 未读总数
     */
    private int unreadTotal;

    public int getUnreadTotal() {
        return commentCount + likeCount + newFollowCount + systemMsgCount + chatMsgCount;
    }
}
