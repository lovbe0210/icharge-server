package com.lovbe.icharge.service.impl;

import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.dao.SocialLikeDao;
import com.lovbe.icharge.entity.dto.LikeActionDo;
import com.lovbe.icharge.service.SocialLikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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
    public void handlerLikeAction(List<LikeActionDo> actionDoList) {
        // 1. 获取数据库中收藏状态
        List<LikeActionDo> likeActionList = socialLikeDao.selectListByAction(actionDoList);
        Map<String, LikeActionDo> actionMap = CollectionUtils.isEmpty(likeActionList) ? Collections.EMPTY_MAP :
                likeActionList.stream().collect(Collectors.toMap(action -> action.getUserId() + "-" + action.getTargetId(),
                        Function.identity(),
                        (a, b) -> b));

        // 2. 根据回源数据进行自我校正
        List<LikeActionDo> statisticAddList = new ArrayList<>();
        List<LikeActionDo> statisticSubList = new ArrayList<>();
        List<LikeActionDo> likeActionUpdateList = new ArrayList<>();
        List<Long> likeActionDeleteList = new ArrayList<>();
        Map<String, List<LikeActionDo>> collectMap = actionDoList.stream()
                .collect(Collectors.groupingBy(action -> action.getUserId() + "-" + action.getTargetId()));
        collectMap.forEach((key, list) -> {
            LikeActionDo actionDB = actionMap.get(key);
            LikeActionDo action = list.get(0);
            if (list.size() == 1) {
                if (actionDB != null) {
                    // 已存在点赞数据
                    if (action.getAction() == 1) {
                        // 更新点赞时间即可
                        actionDB.setUpdateTime(action.getCreateTime());
                        likeActionUpdateList.add(actionDB);
                    } else {
                        // 取消点赞
                        likeActionDeleteList.add(actionDB.getUid());
                        statisticSubList.add(action);
                    }
                } else {
                    // 不存在点赞数据且为点赞动作
                    if (action.getAction() == 1) {
                        action.setUid(YitIdHelper.nextId())
                                .setUpdateTime(action.getCreateTime())
                                .setStatus(CommonStatusEnum.NORMAL.getStatus());
                        likeActionUpdateList.add(action);
                        statisticAddList.add(action);
                    }
                }
            } else {
                AtomicInteger likeFlag = new AtomicInteger(actionDB == null ? 0 : 1);
                list.sort((o1, o2) -> o1.getCreateTime().compareTo(o2.getCreateTime()));
                list.forEach(ad -> {
                    if (ad.getAction() == 1 && likeFlag.get() == 0) {
                        likeFlag.incrementAndGet();
                    } else if (ad.getAction() == 0 && likeFlag.get() == 1) {
                        likeFlag.decrementAndGet();
                    }
                });
                if (likeFlag.get() == 1 && list.get(list.size() - 1).getAction() == 1) {
                    // 获取最后一条消息的时间更新点赞时间
                    if (actionDB != null) {
                        actionDB.setUpdateTime(list.get(list.size() - 1).getCreateTime());
                        likeActionDeleteList.add(actionDB.getUid());
                    } else {
                        LikeActionDo likeAction = list.get(list.size() - 1);
                        likeAction.setUid(YitIdHelper.nextId())
                                .setUpdateTime(likeAction.getCreateTime())
                                .setStatus(CommonStatusEnum.NORMAL.getStatus());
                        likeActionUpdateList.add(likeAction);
                        statisticAddList.add(likeAction);
                    }
                } else if (actionDB != null && likeFlag.get() == 0 && list.get(list.size() - 1).getAction() == 0) {
                    // 取消点赞
                    likeActionDeleteList.add(actionDB.getUid());
                    statisticSubList.add(actionDB);
                }
            }
        });


        // 2. 数据持久化
        if (statisticAddList.size() > 0) {
            socialLikeDao.updateStatisticByAdd(statisticAddList);
        }
        if (statisticSubList.size() > 0) {
            socialLikeDao.updateStatisticBySub(statisticSubList);
        }
        if (likeActionUpdateList.size() > 0) {
            socialLikeDao.insertOrUpdate(likeActionUpdateList);
        }
        if (likeActionDeleteList.size() > 0) {
            socialLikeDao.deleteByIds(likeActionDeleteList);
        }




        // 更新redis
        /*String likesSetKey = RedisKeyConstant.getUserLikesSet(actionDo.getUserId());
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
            RedisUtil.zSetTuple(likesSetKey, typedTupleSet);
        }
        RedisUtil.zset(likesSetKey,
                actionDo.getCreateTime() == null ? System.currentTimeMillis() : actionDo.getCreateTime().getTime(),
                actionDo.getTargetId());
        // 如果长度大于999，只保留999
        RedisUtil.szRemoveRange(likesSetKey, 999);*/
    }
}
