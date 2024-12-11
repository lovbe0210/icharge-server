package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.LikeActionDo;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 22:57
 * @Description: 社交点赞数据流处理服务
 */
public interface SocialLikeService {
    /**
     * @description: 点赞数据流逻辑处理
     * @param: LikeActionDo
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/8 22:59
     */
    void handlerLikeAction(List<LikeActionDo> actionList);
}
