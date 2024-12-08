package com.lovbe.icharge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.dao.SocialLikeDao;
import com.lovbe.icharge.entity.dto.LikeActionDo;
import com.lovbe.icharge.service.SocialLikeService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 22:58
 * @Description: MS
 */
@Service
public class SocialLikeServiceImpl implements SocialLikeService {
    @Resource
    private SocialLikeDao socialLikeDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handlerLikeAction(LikeActionDo actionDo) {
        // 判断redis中是否有最近的点赞记录
        Long userId = actionDo.getUserId();
        String likesSet = RedisKeyConstant.getUserLikesSet(userId);
        if (RedisUtil.zsHasValue(likesSet, actionDo.getTargetId())) {
            // 已经点过赞了,对数据库进行操作，取消点赞
            socialLikeDao.delete(new LambdaQueryWrapper<LikeActionDo>()
                    .eq(LikeActionDo::getUserId, actionDo.getUserId())
                    .eq(LikeActionDo::getTargetId, actionDo.getTargetId()));
            // redis和数据库数据保持一致,移出redis中的点赞数据
            RedisUtil.zRemove(likesSet, actionDo.getTargetId());
        } else {
            // 未点赞，进行点赞操作，查询数据库是否包含点赞信息
            LikeActionDo selected = socialLikeDao.selectOne(new LambdaQueryWrapper<LikeActionDo>()
                    .eq(LikeActionDo::getUserId, actionDo.getUserId())
                    .eq(LikeActionDo::getTargetId, actionDo.getTargetId()), false);
            if (selected != null) {
                // 已经包含点赞信息，数据库更新updateTime
                selected.setUpdateTime(new Date());
                socialLikeDao.updateById(selected);
            } else {
                actionDo.setUid(YitIdHelper.nextId())
                        .setStatus(CommonStatusEnum.NORMAL.getStatus())
                        .setCreateTime(new Date())
                        .setUpdateTime(new Date());
                socialLikeDao.insert(actionDo);
            }
            // 更新redis
            Page<LikeActionDo> likeActionList = socialLikeDao.selectPage(new Page<>(0, 998), new LambdaQueryWrapper<LikeActionDo>()
                    .eq(LikeActionDo::getUserId, actionDo.getUserId())
                    .orderByDesc(LikeActionDo::getUpdateTime));
            if (!CollectionUtils.isEmpty(likeActionList.getRecords())) {
                Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = likeActionList.getRecords().parallelStream()
                        .map(action -> {
                            Long targetId = action.getTargetId();
                            long time = action.getUpdateTime() == null ? 0 : action.getUpdateTime().getTime();
                            ZSetOperations.TypedTuple<Object> tuple = ZSetOperations.TypedTuple.of(targetId, time * 1.00);
                            return tuple;
                        }).collect(Collectors.toSet());
                RedisUtil.zSetTuple(likesSet, typedTupleSet);
            }
            RedisUtil.zset(likesSet,
                    actionDo.getCreateTime() == null ? System.currentTimeMillis() : actionDo.getCreateTime().getTime(),
                    actionDo.getTargetId());
            // 如果长度大于999，只保留999
            RedisUtil.szRemoveRange(likesSet, 999);
        }
    }
}
