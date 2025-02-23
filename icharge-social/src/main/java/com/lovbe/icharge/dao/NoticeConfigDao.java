package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import com.lovbe.icharge.entity.dto.EnableSocialDTO;
import com.lovbe.icharge.entity.dto.NoticeConfigDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 23:57
 * @Description: 消息通知设置
 */
@Mapper
public interface NoticeConfigDao extends BaseMapper<NoticeConfigDo> {
    /**
     * @description: 通过点赞操作获取用户消息设置
     * @param: targetId
     * @param: targetType
     * @return: com.lovbe.icharge.entity.dto.NoticeConfigDo
     * @author: lovbe0210
     * @date: 2025/2/22 22:20
     */
    EnableSocialDTO selectEnableSocialNotice(@Param("userId") Long userId, @Param("likeData") ContentLikeDTO likeData);
}
