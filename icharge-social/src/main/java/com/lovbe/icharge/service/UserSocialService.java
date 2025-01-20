package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.ReplyCommentVo;

import java.util.List;

/**
 * @description: 用户相关社交交互
 * @author: Lvhl
 * @date: 2024/5/9 13:20
 */
public interface UserSocialService {

    /**
     * @description: 用户关注操作
     * @param: baseRequest
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/1/20 17:21
     */
    void userActionFollow(BaseRequest<TargetFollowDTO> baseRequest, Long userId);

    /**
     * @description: 获取用户关系
     * @param: targetUser
     * @param: userId
     * @return: com.lovbe.icharge.entity.dto.RelationshipDo
     * @author: lovbe0210
     * @date: 2025/1/20 22:41
     */
    RelationshipDo getRelationship(Long targetUser, Long userId);
}
