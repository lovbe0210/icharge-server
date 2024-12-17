package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.entity.dto.ReplyCommentDo;
import com.lovbe.icharge.entity.dto.TargetCommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 23:57
 * @Description: 评论回复
 */
@Mapper
public interface ReplyCommentDao extends BaseMapper<ReplyCommentDo> {
    /**
     * @description: 获取评论统计
     * @param: TargetCommentDTO
     * @return: TargetStatisticDo
     * @author: lovbe0210
     * @date: 2024/12/18 0:44
     */
    TargetStatisticDo selectCommentStatistic(@Param("targetComment") TargetCommentDTO data);

    /**
     * @description: 获取评论列表
     * @param: TargetCommentDTO
     * @return: List<ReplyCommentDo>
     * @author: lovbe0210
     * @date: 2024/12/18 1:07
     */
    List<ReplyCommentDo> selectReplyCommentList(@Param("commentDTO") TargetCommentDTO commentDTO);
}
