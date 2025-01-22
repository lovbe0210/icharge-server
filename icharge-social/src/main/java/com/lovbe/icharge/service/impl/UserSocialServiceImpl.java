package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.vo.RelationshipVo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.dao.SocialFollowDao;
import com.lovbe.icharge.entity.dto.RelationshipDo;
import com.lovbe.icharge.entity.dto.TargetFollowDTO;
import com.lovbe.icharge.service.UserSocialService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/20 17:21
 * @Description: MS
 */
@Slf4j
@Service
public class UserSocialServiceImpl implements UserSocialService {
    @Resource
    private SocialFollowDao followDao;
    @Resource
    private CommonService commonService;
    // 用户操作：关注/取消关注
    @Value("${spring.kafka.topics.user-action-follow}")
    private String followTopic;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public void userActionFollow(BaseRequest<TargetFollowDTO> baseRequest, Long userId) {
        TargetFollowDTO data = baseRequest.getData();
        Long masterId = Math.max(data.getTargetUser(), userId);
        Long slaveId = Math.min(data.getTargetUser(), userId);
        RelationshipDo relationship = new RelationshipDo()
                .setUid(masterId + SysConstant.SEPARATOR + slaveId)
                .setUserIdMaster(masterId)
                .setUserIdSlave(slaveId);
        if (Objects.equals(masterId, userId)) {
            relationship.setMasterWatchSlave(data.getAction())
                    .setSlaveWatchMaster(0);
        } else if (Objects.equals(slaveId, userId)) {
            relationship.setSlaveWatchMaster(data.getAction())
                    .setMasterWatchSlave(0);
        }
        followDao.updateRelationShip(relationship, Objects.equals(masterId, userId));
        // 发送用户关注或取消关注的消息，及逆行异步统计关注和粉丝数
        data.setUserId(userId);
        commonService.sendMessage(appName, followTopic, data);
    }

    @Override
    public RelationshipDo getRelationship(Long targetUser, Long userId) {
        long masterId = Math.max(targetUser, userId);
        long slaveId = Math.min(targetUser, userId);
        String uid = masterId + SysConstant.SEPARATOR + slaveId;
        RelationshipDo relationship = followDao.selectById(uid);
        return relationship;
    }

    @Override
    public List<RelationshipVo> getRelationshipList(Long userId, String targetShip) {
        List<RelationshipDo> relationshipList = null;
        if (SysConstant.RELATIONSHIP_FOLLOW.equals(targetShip)) {
            relationshipList = followDao.selectFollowList(userId);
        } else if (SysConstant.RELATIONSHIP_FANS.equals(targetShip)) {
            relationshipList = followDao.selectFansList(userId);
        }
        if (CollectionUtils.isEmpty(relationshipList)) {
            return List.of();
        }
        return relationshipList.stream()
                .map(relationship -> {
                    RelationshipVo relationshipVo = new RelationshipVo()
                            .setFollowActionId(relationship.getUid())
                            .setUpdateTime(relationship.getUpdateTime())
                            .setIsEachFollow(relationship.getMasterWatchSlave() == 1 && relationship.getSlaveWatchMaster() == 1 ? 1 : 0);
                    if (Objects.equals(relationship.getUserIdMaster(), userId)) {
                        relationshipVo.setUid(relationship.getUserIdSlave());
                    } else {
                        relationshipVo.setUid(relationship.getUserIdMaster());
                    }
                    UserInfoDo userInfoDo = commonService.getCacheUser(relationshipVo.getUid());
                    relationshipVo.setAvatarUrl(userInfoDo.getAvatarUrl())
                            .setUsername(userInfoDo.getUsername())
                            .setDomain(userInfoDo.getDomain())
                            .setTags(userInfoDo.getTags())
                            .setLevel(userInfoDo.getLevel())
                            .setIndustry(userInfoDo.getIndustry())
                            .setIntroduction(userInfoDo.getIntroduction())
                            .setLocation(userInfoDo.getLocation())
                            .setStatus(userInfoDo.getStatus());
                    return relationshipVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TargetStatisticDo getRelationShipStatistic(Long userId) {
        TargetStatisticDo statisticDo = followDao.selectShipStatistic(userId);
        return statisticDo == null ? new TargetStatisticDo() : statisticDo;
    }
}
