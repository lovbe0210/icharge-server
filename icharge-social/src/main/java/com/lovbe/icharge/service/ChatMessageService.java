package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.ConversationDTO;
import com.lovbe.icharge.entity.dto.WsMessageDTO;
import com.lovbe.icharge.entity.vo.MessageSessionVo;
import com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo;

import java.util.List;

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

    /**
     * @description: 获取会话列表
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.vo.MessageSessionVo>
     * @author: lovbe0210
     * @date: 2025/2/28 16:51
     */
    List<MessageSessionVo> getSessionList(Long userId);

    /**
     * @description: 回调接口处理，填充数据
     * @param: wsMessageDTO
     * @return: com.lovbe.icharge.entity.dto.WsMessageDTO<java.lang.Object>
     * @author: lovbe0210
     * @date: 2025/2/28 22:58
     */
    WsMessageDTO<Object> scheduleCallback(WsMessageDTO wsMessageDTO);

    /**
     * @return sessionId
     * @description: 开始消息会话
     * @param: data
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/3/1 23:26
     */
    Long startMessageSession(ConversationDTO data, Long userId);
}
