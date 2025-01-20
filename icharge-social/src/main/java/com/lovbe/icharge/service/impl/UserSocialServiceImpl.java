package com.lovbe.icharge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.dao.SocialFollowDao;
import com.lovbe.icharge.entity.dto.RelationshipDo;
import com.lovbe.icharge.entity.dto.TargetFollowDTO;
import com.lovbe.icharge.service.UserSocialService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    }

    @Override
    public RelationshipDo getRelationship(Long targetUser, Long userId) {
        long masterId = Math.max(targetUser, userId);
        long slaveId = Math.min(targetUser, userId);
        String uid = masterId + SysConstant.SEPARATOR + slaveId;
        RelationshipDo relationship = followDao.selectById(uid);
        return relationship;
    }
}
