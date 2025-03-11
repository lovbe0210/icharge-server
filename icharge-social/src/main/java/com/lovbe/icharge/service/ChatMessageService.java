package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.dto.UploadDTO;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.MessageActionVo;
import com.lovbe.icharge.entity.vo.MessageSessionVo;
import com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo;
import jakarta.validation.constraints.NotNull;

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

    /**
     * @description: 获取聊天记录
     * @param: userId
     * @param: sessionId
     * @return: java.util.List<com.lovbe.icharge.entity.vo.ChatMessageVo>
     * @author: lovbe0210
     * @date: 2025/3/6 15:07
     */
    List<MessageConfirmVo> getChatLogList(Long userId, Long sessionId, int offset, int limit);

    /**
     * @description: 会话状态更新
     * @param: data
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/3/7 22:27
     */
    void updateMessageSession(ConversationUpdateDTO data, Long userId);

    /**
     * @description: 会话删除
     * @param: data
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/3/7 23:11
     */
    void deleteMessageSession(ConversationUpdateDTO data, Long userId);

    /**
     * @description: 消息删除
     * @param: messageId
     * @param: userId
     * @return: com.lovbe.icharge.entity.vo.MessageActionVo
     * @author: lovbe0210
     * @date: 2025/3/8 13:33
     */
    MessageActionVo deleteMessageLog(Long messageId, Long userId);

    /**
     * @description: 消息撤回
     * @param: messageId
     * @param: userId
     * @return: com.lovbe.icharge.entity.vo.MessageActionVo
     * @author: lovbe0210
     * @date: 2025/3/8 13:34
     */
    MessageActionVo rollbackMessageLog(Long messageId, Long userId);

    /**
     * @description: 发送聊天记录文件
     * @param: uploadDTO
     * @param: userId
     * @return: java.lang.String
     * @author: lovbe0210
     * @date: 2025/3/11 23:06
     */
    String uploadChatFile(UploadDTO uploadDTO, Long userId);
}
