package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.ConversationDo;
import com.lovbe.icharge.entity.dto.SocialNoticeDo;
import com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 23:57
 * @Description: 会话操作
 */
@Mapper
public interface ConversationDao extends BaseMapper<ConversationDo> {

}
