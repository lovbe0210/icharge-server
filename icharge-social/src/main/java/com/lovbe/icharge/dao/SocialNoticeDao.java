package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 23:57
 * @Description: 社交通知
 */
@Mapper
public interface SocialNoticeDao extends BaseMapper<SocialNoticeDo> {
    /**
     * @description: 获取未读统计数据
     * @param: userId
     * @return: com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo
     * @author: lovbe0210
     * @date: 2025/2/24 0:21
     */
    UnreadMsgStatisticVo selectUnreadStatistic(@Param("userId") Long userId);

    /**
     * @description: 获取评论通知列表
     * @param: data
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.dto.SocialNoticeDo>
     * @author: lovbe0210
     * @date: 2025/2/24 16:43
     */
    List<SocialNoticeDo> selectCommentListCount(@Param("data") SocialNoticeReqDTO data, @Param("userId") Long userId);

    /**
     * @description: 获取点赞通知列表
     * @param: data
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.dto.SocialNoticeDo>
     * @author: lovbe0210
     * @date: 2025/2/25 15:56
     */
    List<SocialNoticeDo> selectLikeListCount(@Param("data") SocialNoticeReqDTO data, @Param("userId") Long userId);
}
