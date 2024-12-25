package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.LikeActionDo;
import com.lovbe.icharge.entity.dto.ReplyCommentDo;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 22:57
 * @Description: 社交点赞数据流处理服务
 */
public interface ActionHandlerService {
    /**
     * @description: 点赞数据流逻辑处理
     * @param: LikeActionDo
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/8 22:59
     */
    void handlerLikeAction(List<LikeActionDo> actionList);

    /**
     * @description: 评论数据流逻辑处理
     * @param: List<LikeActionDo>
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/21 10:09
     */
    void handlerCommentAction(List<ReplyCommentDo> actionList);
}
