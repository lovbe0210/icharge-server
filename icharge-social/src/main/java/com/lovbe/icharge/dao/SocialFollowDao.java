package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.model.dto.RelationshipDo;
import com.lovbe.icharge.entity.dto.TargetFollowDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 22:48
 * @Description: 社交关注
 */
@Mapper
public interface SocialFollowDao extends BaseMapper<RelationshipDo> {
    /**
     * @description: 获取人际关系
     * @param: targetUser
     * @param: userId
     * @return: com.lovbe.icharge.common.model.dto.RelationshipDo
     * @author: lovbe0210
     * @date: 2025/1/20 20:33
     */
    void updateRelationShip(@Param("relationship") RelationshipDo relationship, @Param("isMaster") boolean isMaster);

    /**
     * @description: 获取我的关注列表
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.common.model.dto.RelationshipDo>
     * @author: lovbe0210
     * @date: 2025/1/21 16:28
     */
    List<RelationshipDo> selectFollowList(@Param("userId") Long userId, @Param("request") RequestListDTO request);

    /**
     * @description: 获取我的粉丝
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.common.model.dto.RelationshipDo>
     * @author: lovbe0210
     * @date: 2025/1/21 21:29
     */
    List<RelationshipDo> selectFansList(@Param("userId") Long userId, @Param("request") RequestListDTO request);

    /**
     * @description: 更新关注数
     * @param: values
     * @author: lovbe0210
     * @date: 2025/1/22 21:12
     */
    void updateFollowCount(@Param("statisticList") Collection<TargetStatisticDo> statisticList);

    /**
     * @description: 获取统计
     * @param: userId
     * @return: com.lovbe.icharge.common.model.dto.TargetStatisticDo
     * @author: lovbe0210
     * @date: 2025/1/22 21:38
     */
    TargetStatisticDo selectShipStatistic(@Param("userId") Long userId);

    /**
     * @description: 获取所有关注用户id
     * @param: userId
     * @return: java.util.List<java.lang.Long>
     * @author: lovbe0210
     * @date: 2025/3/13 0:26
     */
    List<Long> getFollowUserList(@Param("userId") Long userId);

    /**
     * @description: 获取已经添加新增粉丝的激励用户
     * @param: targetFollowList
     * @return: java.util.List<com.lovbe.icharge.entity.dto.TargetFollowDTO>
     * @author: lovbe0210
     * @date: 2025/3/19 20:38
     */
    List<TargetFollowDTO> selectNoEncourageUser(@Param("targetFollowList") List<TargetFollowDTO> targetFollowList,
                                                @Param("behaviorType") int behaviorType);

}
