package com.lovbe.icharge.service.impl;

import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.dao.ReplyCommentDao;
import com.lovbe.icharge.dao.SocialLikeDao;
import com.lovbe.icharge.entity.dto.LikeActionDo;
import com.lovbe.icharge.entity.dto.ReplyCommentDo;
import com.lovbe.icharge.service.SocialActionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 22:58
 * @Description: 社交行为处理逻辑，点赞、评论
 */
@Service
public class SocialActionServiceImpl implements SocialActionService {
    @Resource
    private SocialLikeDao socialLikeDao;
    @Resource
    private ReplyCommentDao replyCommentDao;

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
        Set<Long> userIdSet = new HashSet<>();
        Set<Long> targetIdSet = new HashSet<>();
        collectMap.forEach((key, list) -> {
            LikeActionDo actionDB = actionMap.get(key);
            if (list.size() == 1) {
                handleSingleAction(list.get(0), actionDB, likeActionUpdateList, likeActionDeleteList, statisticAddList, statisticSubList, userIdSet, targetIdSet);
            } else {
                handleMultipleActions(list, actionDB, likeActionDeleteList, likeActionUpdateList, statisticAddList, statisticSubList, userIdSet, targetIdSet);
            }
        });

        // 3. 数据持久化
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

        // 4. 将变动的userId和targetId存入redis，在定时任务中获取变化的userId和target对明细进行update操作
        if (userIdSet.size() > 0) {
            String changeUserKey = RedisKeyConstant.getLikeChangeUserSet();
            RedisUtil.sSet(changeUserKey, userIdSet.toArray());
        }
        if (targetIdSet.size() > 0) {
            String changeTargetKey = RedisKeyConstant.getLikeChangeTargetSet();
            RedisUtil.sSet(changeTargetKey, targetIdSet.toArray());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handlerCommentAction(List<ReplyCommentDo> actionList) {
        // 点赞明细入库
        replyCommentDao.insert(actionList);
        // 过滤出楼中楼回复对父级评论更新统计
        List<ReplyCommentDo> replyList = new ArrayList<>();
        actionList.stream()
                .filter(replyCommentDo -> replyCommentDo.getParentId() != null)
                .collect(Collectors.groupingBy(ReplyCommentDo::getParentId))
                .forEach((parentId, list) -> {
                    ReplyCommentDo replyCommentDo = new ReplyCommentDo().setReplyCount(list.size());
                    replyCommentDo.setUid(parentId);
                    replyList.add(replyCommentDo);
                });
        if (replyList.size() > 0) {
            replyCommentDao.updateReplyCount(replyList);
        }
    }

    /**
     * 多次点赞处理
     *
     * @param list
     * @param actionDB
     * @param likeActionDeleteList
     * @param likeActionUpdateList
     * @param statisticAddList
     * @param statisticSubList
     * @param userIdSet
     * @param targetIdSet
     */
    private static void handleMultipleActions(List<LikeActionDo> list,
                                              LikeActionDo actionDB,
                                              List<Long> likeActionDeleteList,
                                              List<LikeActionDo> likeActionUpdateList,
                                              List<LikeActionDo> statisticAddList,
                                              List<LikeActionDo> statisticSubList,
                                              Set<Long> userIdSet, Set<Long> targetIdSet) {
        LikeActionDo action = list.get(0);
        int likeFlag = actionDB == null ? 0 : 1;
        list.sort((o1, o2) -> o1.getCreateTime().compareTo(o2.getCreateTime()));
        for (LikeActionDo ad : list) {
            if (ad.getAction() == 1 && likeFlag == 0) {
                likeFlag++;
            } else if (ad.getAction() == 0 && likeFlag == 1) {
                likeFlag--;
            }
        }
        if (likeFlag == 1 && list.get(list.size() - 1).getAction() == 1) {
            // 获取最后一条消息的时间更新点赞时间
            if (actionDB != null) {
                actionDB.setUpdateTime(list.get(list.size() - 1).getCreateTime());
                likeActionDeleteList.add(actionDB.getUid());
                statisticSubList.add(actionDB);
            } else {
                LikeActionDo likeAction = list.get(list.size() - 1);
                likeAction.setUid(YitIdHelper.nextId())
                        .setUpdateTime(likeAction.getCreateTime())
                        .setStatus(CommonStatusEnum.NORMAL.getStatus());
                likeActionUpdateList.add(likeAction);
                statisticAddList.add(likeAction);
                userIdSet.add(action.getUserId());
                if (action.getTargetType() < 4) {
                    targetIdSet.add(action.getTargetId());
                }
            }
        } else if (actionDB != null && likeFlag == 0 && list.get(list.size() - 1).getAction() == 0) {
            // 取消点赞
            likeActionDeleteList.add(actionDB.getUid());
            statisticSubList.add(actionDB);
            userIdSet.add(action.getUserId());
            if (action.getTargetType() < 4) {
                targetIdSet.add(action.getTargetId());
            }
        }
    }

    /**
     * 单次点赞处理
     *
     * @param action
     * @param actionDB
     * @param likeActionUpdateList
     * @param likeActionDeleteList
     * @param statisticAddList
     * @param statisticSubList
     * @param userIdSet
     * @param targetIdSet
     */
    private static void handleSingleAction(LikeActionDo action, LikeActionDo actionDB,
                                           List<LikeActionDo> likeActionUpdateList,
                                           List<Long> likeActionDeleteList,
                                           List<LikeActionDo> statisticAddList,
                                           List<LikeActionDo> statisticSubList,
                                           Set<Long> userIdSet, Set<Long> targetIdSet) {
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
                userIdSet.add(action.getUserId());
                if (action.getTargetType() < 4) {
                    targetIdSet.add(action.getTargetId());
                }
            }
        } else {
            // 不存在点赞数据且为点赞动作
            if (action.getAction() == 1) {
                action.setUid(YitIdHelper.nextId())
                        .setUpdateTime(action.getCreateTime())
                        .setStatus(CommonStatusEnum.NORMAL.getStatus());
                likeActionUpdateList.add(action);
                statisticAddList.add(action);
                userIdSet.add(action.getUserId());
                if (action.getTargetType() < 4) {
                    targetIdSet.add(action.getTargetId());
                }
            }
        }
    }
}
