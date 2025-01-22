package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.model.vo.RelationshipVo;
import com.lovbe.icharge.entity.dto.*;

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

    /**
     * @description: 获取关注或粉丝列表
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.common.model.vo.RelationshipVo>
     * @author: lovbe0210
     * @date: 2025/1/21 17:47
     */
    List<RelationshipVo> getRelationshipList(Long userId, String targetShip);
    
    /** 
     * @description: 获取关注和粉丝统计
     * @param: userId 
     * @return: com.lovbe.icharge.common.model.dto.TargetStatisticDo
     * @author: lovbe0210
     * @date: 2025/1/22 21:34
     */
    TargetStatisticDo getRelationShipStatistic(Long userId);
}
