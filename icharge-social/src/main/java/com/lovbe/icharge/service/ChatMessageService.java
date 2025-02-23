package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/23 23:58
 * @Description: 聊天消息
 */
public interface ChatMessageService {
    /**
     * @description: 获取未读消息统计数
     * @param: userId
     * @return: com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo
     * @author: lovbe0210
     * @date: 2025/2/24 0:05
     */
    UnreadMsgStatisticVo getUnreadStatistic(Long userId);
}
