package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.ChatMessageLogDo;
import com.lovbe.icharge.entity.dto.ConversationDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 23:57
 * @Description: 会话操作
 */
@Mapper
public interface ConversationDao extends BaseMapper<ConversationDo> {
    /**
     * @description: 批量获取会话列表
     * @param: conversationUsers
     * @return: java.util.List<com.lovbe.icharge.entity.dto.ConversationDo>
     * @author: lovbe0210
     * @date: 2025/3/4 21:53
     */
    List<ConversationDo> selectListByChatMsg(@Param("chatMessageLogs") List<ChatMessageLogDo> chatMessageLogs);
}
