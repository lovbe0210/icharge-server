package com.lovbe.icharge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.vo.RelationshipVo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.dao.NoticeConfigDao;
import com.lovbe.icharge.dao.SocialFollowDao;
import com.lovbe.icharge.common.model.dto.RelationshipDo;
import com.lovbe.icharge.dao.SocialNoticeDao;
import com.lovbe.icharge.entity.dto.NoticeConfigDo;
import com.lovbe.icharge.entity.dto.SocialNoticeDo;
import com.lovbe.icharge.entity.dto.TargetFollowDTO;
import com.lovbe.icharge.service.UserSocialService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
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
    private NoticeConfigDao noticeConfigDao;
    @Resource
    private SocialNoticeDao socialNoticeDao;
    @Resource
    private CommonService commonService;
    // 用户操作：关注/取消关注
    @Value("${spring.kafka.topics.user-action-follow}")
    private String followTopic;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userActionFollow(BaseRequest<TargetFollowDTO> baseRequest, Long userId) {
        TargetFollowDTO data = baseRequest.getData();
        data.setUserId(userId);
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
        if (data.getAction() == 1) {
            // 获取关注用户的消息通知设置
            NoticeConfigDo noticeConfig = noticeConfigDao.selectOne(new LambdaQueryWrapper<NoticeConfigDo>()
                    .eq(NoticeConfigDo::getUid, data.getTargetUser()), false);
            if (noticeConfig == null || noticeConfig.getNewFollowerMsg() == 1) {
                // 开启通知
                SocialNoticeDo noticeDo = new SocialNoticeDo()
                        .setUserId(data.getTargetUser())
                        .setNoticeType(SysConstant.NOTICE_FOLLOW)
                        .setTargetId(0L)
                        .setActionUserId(userId);
                noticeDo.setUid(YitIdHelper.nextId())
                        .setStatus(CommonStatusEnum.NORMAL.getStatus())
                        .setCreateTime(new Date())
                        .setUpdateTime(noticeDo.getCreateTime());
                socialNoticeDao.insert(noticeDo);
            }
        } else {
            socialNoticeDao.delete(new QueryWrapper<SocialNoticeDo>()
                    .eq("user_id", data.getTargetUser())
                    .eq("action_user_id", userId)
                    .eq("notice_type", SysConstant.NOTICE_FOLLOW));
        }
        // 发送用户关注或取消关注的消息，进行异步统计关注和粉丝数
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
    public PageBean<RelationshipVo> getFollowFansList(Long userId, RequestListDTO request, String targetShip) {
        List<RelationshipDo> relationshipList = null;
        int total = 0;
        TargetStatisticDo statisticDo = followDao.selectShipStatistic(userId);
        if (SysConstant.RELATIONSHIP_FOLLOW.equals(targetShip)) {
            if (statisticDo == null || statisticDo.getFollowCount() == 0) {
                return new PageBean<>(0, List.of());
            }
            total = statisticDo.getFollowCount();
            relationshipList = followDao.selectFollowList(userId, request);
        } else if (SysConstant.RELATIONSHIP_FANS.equals(targetShip)) {
            if (statisticDo == null || statisticDo.getFansCount() == 0) {
                return new PageBean<>(0, List.of());
            }
            total = statisticDo.getFansCount();
            relationshipList = followDao.selectFansList(userId, request);
        }
        List<RelationshipVo> collect = relationshipList.stream()
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
        return new PageBean<>(total, collect);
    }

    @Override
    public TargetStatisticDo getRelationShipStatistic(Long userId) {
        TargetStatisticDo statisticDo = followDao.selectShipStatistic(userId);
        return statisticDo == null ? new TargetStatisticDo() : statisticDo;
    }

    @Override
    public List<RelationshipVo> getRelationshipList(List<Long> userIdList, Long userId) {
        List<String> collect = userIdList.stream().map(targetUser -> {
            long masterId = Math.max(targetUser, userId);
            long slaveId = Math.min(targetUser, userId);
            return masterId + SysConstant.SEPARATOR + slaveId;
        }).collect(Collectors.toList());
        List<RelationshipDo> relationships = followDao.selectBatchIds(collect);
        if (CollectionUtils.isEmpty(relationships)) {
            return List.of();
        }
        List<RelationshipVo> relationshipList = relationships.stream()
                .filter(ship -> {
                    if (Objects.equals(ship.getUserIdMaster(), userId)) {
                        return Objects.equals(ship.getMasterWatchSlave(), 1);
                    }
                    return Objects.equals(ship.getSlaveWatchMaster(), 1);
                })
                .map(ship -> {
                    RelationshipVo relationshipVo = new RelationshipVo()
                            .setFollowActionId(ship.getUid())
                            .setIsEachFollow(Objects.equals(ship.getMasterWatchSlave(), 1) &&
                                    Objects.equals(ship.getSlaveWatchMaster(), 1) ? 1 : 0);
                    ;
                    relationshipVo.setUid(Objects.equals(ship.getUserIdMaster(), userId) ? ship.getUserIdSlave() : ship.getUserIdSlave());
                    return relationshipVo;
                })
                .collect(Collectors.toList());
        return relationshipList;
    }
}
