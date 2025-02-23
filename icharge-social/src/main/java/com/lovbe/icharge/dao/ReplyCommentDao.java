package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.entity.dto.EnableSocialDTO;
import com.lovbe.icharge.entity.dto.ReplyCommentDTO;
import com.lovbe.icharge.entity.dto.ReplyCommentDo;
import com.lovbe.icharge.entity.dto.TargetCommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
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

    /**
     * @description: 获取回复列表
     * @param: TargetCommentDTO
     * @return: List<ReplyCommentDo>
     * @author: lovbe0210
     * @date: 2024/12/20 19:10
     */
    List<ReplyCommentDo> selectCommentReplyList(@Param("commentDTO") TargetCommentDTO commentDTO);

    /**
     * @description: 更新评论的回复数
     * @param: List<ReplyCommentDo>
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/21 11:20
     */
    void updateCommentCount(@Param("statisticList") Collection<TargetStatisticDo> statisticList);

    /**
     * @description: 统计数减1
     * @param: Long
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/22 22:46
     */
    void updateReplyCountBySub(@Param("uid") Long uid, @Param("deleteCount") int deleteCount);

    /**
     * @description: 删除统计表
     * @param: Long
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/22 22:51
     */
    void deleteStatistic(@Param("uid") Long uid);

    /**
     * @description: 判断能否发送评论
     * @param: replyCommentDTO
     * @return: int
     * @author: lovbe0210
     * @date: 2025/2/11 23:08
     */
    EnableSocialDTO selectEnableComment(@Param("replyCommentDTO") ReplyCommentDTO replyCommentDTO);
}
