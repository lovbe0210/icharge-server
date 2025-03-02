package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.ChatMessageLogDo;
import com.lovbe.icharge.entity.dto.ConversationDo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 23:57
 * @Description: 消息记录
 */
@Mapper
public interface ChatMessageLogDao extends BaseMapper<ChatMessageLogDo> {

}
