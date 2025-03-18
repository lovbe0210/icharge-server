package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.SocialNoticeDo;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.model.vo.PublicArticleVo;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.config.SessionManager;
import com.lovbe.icharge.dao.*;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.service.ActionHandlerService;
import com.lovbe.icharge.service.ChatMessageService;
import com.lovbe.icharge.service.feign.ContentPickService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class ActionHandlerServiceImpl implements ActionHandlerService {
    @Resource
    private SocialLikeDao socialLikeDao;
    @Resource
    private ReplyCommentDao replyCommentDao;
    @Resource
    private SocialFollowDao socialFollowDao;
    @Resource
    private SocialNoticeDao socialNoticeDao;
    @Resource
    private ChatMessageLogDao messageLogDao;
    @Resource
    private ConversationDao conversationDao;
    @Resource
    private NoticeConfigDao noticeConfigDao;
    @Resource
    private ChatMessageService messageService;
    @Resource
    private ContentPickService contentPickService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handlerLikeAction(List<LikeActionDo> actionDoList) {
        // 1. 获取数据库中点赞状态
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
                handleMultipleActions(list, actionDB, likeActionUpdateList, likeActionDeleteList, statisticAddList, statisticSubList, userIdSet, targetIdSet);
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
            List<SocialNoticeDo> noticeList = likeActionUpdateList.stream()
                    .filter(action -> action.getTargetUserId() != null)
                    .map(action -> {
                        SocialNoticeDo noticeDo = new SocialNoticeDo()
                                .setUserId(action.getTargetUserId())
                                .setNoticeType(SysConstant.NOTICE_LIKE)
                                .setTargetId(action.getTargetId())
                                .setActionUserId(action.getUserId());
                        noticeDo.setUid(action.getUid())
                                .setCreateTime(action.getUpdateTime())
                                .setUpdateTime(action.getUpdateTime())
                                .setStatus(CommonStatusEnum.NORMAL.getStatus());
                        return noticeDo;
                    }).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(noticeList)) {
                socialNoticeDao.insertOrUpdate(noticeList);
            }
            // 添加点赞激励
            addEncourageLog(likeActionUpdateList);
        }
        if (likeActionDeleteList.size() > 0) {
            socialLikeDao.deleteByIds(likeActionDeleteList);
            socialNoticeDao.deleteByIds(likeActionDeleteList);
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

    /**
     * @description: 添加点赞激励
     * @param: likeActionUpdateList
     * @author: lovbe0210
     * @date: 2025/3/19 0:06
     */
    private void addEncourageLog(List<LikeActionDo> likeActionUpdateList) {
        Map<Integer, List<Long>> listMap = likeActionUpdateList.stream()
                .filter(likeAction -> likeAction.isNewAction() &&
                        (Objects.equals(SysConstant.TARGET_TYPE_ARTICLE, likeAction.getTargetType()) &&
                                Objects.equals(SysConstant.TARGET_TYPE_ESSAY, likeAction.getTargetType())))
                .collect(Collectors.groupingBy(LikeActionDo::getTargetType,
                                Collectors.mapping(LikeActionDo::getTargetId, Collectors.toList())
                        )
                );
        // 获取文章和随笔信息
        listMap.forEach((targetType, list) -> {
            if (Objects.equals(targetType, SysConstant.TARGET_TYPE_ARTICLE)) {
                ResponseBean<List<PublicArticleVo>> articleList = contentPickService.getArticleListByIds(new BaseRequest<>(list), null);
                if (articleList != null && !CollectionUtils.isEmpty(articleList.getData())) {
                    for (PublicArticleVo datum : articleList.getData()) {
                        // TODO 激励电磁批量入库
                    }
                }
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handlerCommentAction(List<ReplyCommentDo> actionList) {
        // 评论明细入库
        replyCommentDao.insert(actionList);
        // 通知明细
        List<SocialNoticeDo> noticeList = new ArrayList<>();
        // 过滤出楼中楼回复对父级评论更新统计
        Map<Long, TargetStatisticDo> statisticMap = new HashMap<>();
        actionList.stream()
                .peek(replyCommentDo -> {
                    TargetStatisticDo statisticDo = statisticMap.get(replyCommentDo.getTargetId());
                    if (statisticDo == null) {
                        statisticDo = new TargetStatisticDo()
                                .setCommentCount(1)
                                .setType(replyCommentDo.getTargetType());
                        statisticDo.setUid(replyCommentDo.getTargetId());
                        statisticMap.put(replyCommentDo.getTargetId(), statisticDo);
                    } else {
                        statisticDo.setCommentCount(statisticDo.getCommentCount() + 1);
                    }
                    // targetUserId为null不通知,自己给自己评论不通知
                    if (replyCommentDo.getTargetUserId() == null) {
                        return;
                    }
                    SocialNoticeDo noticeDo = new SocialNoticeDo()
                            .setUserId(replyCommentDo.getTargetUserId())
                            .setNoticeType(replyCommentDo.getParentId() == null ? SysConstant.NOTICE_COMMENT : SysConstant.NOTICE_REPLY)
                            .setTargetId(replyCommentDo.getTargetId())
                            .setActionUserId(replyCommentDo.getUserId())
                            .setCommentId(replyCommentDo.getParentId() == null ? replyCommentDo.getUid() : replyCommentDo.getParentId())
                            .setReplyId(replyCommentDo.getParentId() == null ? null : replyCommentDo.getUid());
                    noticeDo.setUid(replyCommentDo.getUid())
                            .setStatus(CommonStatusEnum.NORMAL.getStatus())
                            .setCreateTime(new Date())
                            .setUpdateTime(noticeDo.getCreateTime());
                    noticeList.add(noticeDo);
                })
                .filter(replyCommentDo -> replyCommentDo.getParentId() != null)
                .collect(Collectors.groupingBy(ReplyCommentDo::getParentId))
                .forEach((parentId, list) -> {
                    TargetStatisticDo statisticDo = statisticMap.get(parentId);
                    ReplyCommentDo commentDo = list.get(0);
                    if (statisticDo == null) {
                        statisticDo = new TargetStatisticDo()
                                .setCommentCount(list.size())
                                .setType(commentDo.getTargetType());
                        statisticDo.setUid(parentId);
                        statisticMap.put(parentId, statisticDo);
                    } else {
                        statisticDo.setCommentCount(statisticDo.getCommentCount() + list.size());
                    }
                });
        if (statisticMap.size() > 0) {
            replyCommentDao.updateCommentCount(statisticMap.values());
        }
        if (noticeList.size() > 0) {
            socialNoticeDao.insert(noticeList);
        }
    }

    @Override
    public void handlerFollowAction(List<TargetFollowDTO> collect) {
        // 按userId进行分组统计，作为关注数量的变化
        Map<Long, TargetStatisticDo> statisticMap = collect.stream()
                .peek(followDto -> followDto.setAction(followDto.getAction() == 1 ? 1 : -1))
                .collect(Collectors.groupingBy(TargetFollowDTO::getUserId))
                .values().stream()
                .map(list -> {
                    TargetStatisticDo statisticDo = new TargetStatisticDo();
                    statisticDo.setUid(list.get(0).getUserId());
                    statisticDo.setType(SysConstant.TARGET_TYPE_AUTHOR)
                            .setFollowCount(list.stream().mapToInt(TargetFollowDTO::getAction).sum());
                    return statisticDo;
                })
                .collect(Collectors.toMap(TargetStatisticDo::getUid, Function.identity(), (a, b) -> b));
        // 按targetUser进行分组统计，作为粉丝数量的变化
        collect.stream()
                .collect(Collectors.groupingBy(TargetFollowDTO::getTargetUser))
                .values().forEach(list -> {
                    int fans = list.stream().mapToInt(TargetFollowDTO::getAction).sum();
                    Long targetUser = list.get(0).getTargetUser();
                    TargetStatisticDo statisticDo = statisticMap.get(targetUser);
                    if (statisticDo != null) {
                        statisticDo.setFansCount(fans);
                    } else {
                        statisticDo = new TargetStatisticDo();
                        statisticDo.setUid(targetUser);
                        statisticDo.setType(SysConstant.TARGET_TYPE_AUTHOR)
                                .setFansCount(fans);
                        statisticMap.put(targetUser, statisticDo);
                    }
                });
        // 更新统计表
        socialFollowDao.updateFollowCount(statisticMap.values());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerChatLog(List<ChatMessageLogDo> collect) {
        HashSet<Long> recvIds = new HashSet<>();
        // 获取sendId和recvId对应的消息记录
        Map<String, List<ChatMessageLogDo>> chatMessageMap = collect.stream()
                .peek(chatLog -> recvIds.add(chatLog.getRecvId()))
                .collect(Collectors.groupingBy(chatLog ->
                        Math.max(chatLog.getSendId(), chatLog.getRecvId()) +
                                SysConstant.SEPARATOR + Math.min(chatLog.getSendId(), chatLog.getRecvId())));
        List<ChatMessageLogDo> conversationUsers = chatMessageMap.values().stream()
                .map(list -> list.get(0))
                .collect(Collectors.toList());
        List<ConversationDo> conversationList = conversationDao.selectListByChatMsg(conversationUsers);
        Map<String, ConversationDo> conversationMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(conversationList)) {
            conversationList.forEach(cs -> conversationMap.put(cs.getOwnerUserId() + SysConstant.SEPARATOR + cs.getTargetUserId(), cs));
        }

        // 获取消息通知设置
        List<NoticeConfigDo> configList = noticeConfigDao.selectBatchIds(recvIds);
        Map<Long, NoticeConfigDo> configMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(configList)) {
            configList.forEach(config -> configMap.put(config.getUid(), config));
        }

        // 遍历每个会话进行会话创建和消息推送
        Set<Long> unreadUpdateSet = new HashSet<>();
        Iterator<ChatMessageLogDo> iterator = collect.iterator();
        while (iterator.hasNext()) {
            ChatMessageLogDo chatLog = iterator.next();
            String sendCvsId = chatLog.getSendId() + SysConstant.SEPARATOR + chatLog.getRecvId();
            String recCvsId = chatLog.getRecvId() + SysConstant.SEPARATOR + chatLog.getSendId();
            ConversationDo sendConversation = conversationMap.get(sendCvsId);
            ConversationDo recConversation = conversationMap.get(recCvsId);

            // 发送人消息确认
            if (sendConversation != null && CommonStatusEnum.isNormal(sendConversation.getStatus())) {
                // 进行推送，首先判断自己能否发出消息
                boolean success = sendUserMsgConfirm(sendConversation, recConversation, configMap, chatLog);
                if (!success) {
                    // 发送失败，直接不用再给对方发送通知了
                    iterator.remove();
                    continue;
                }
            }

            // 接收人消息通知
            if (configMap.get(chatLog.getRecvId()) != null && configMap.get(chatLog.getRecvId()).getEnableChatMessage() == 0) {
                // 对方不接收私聊消息
                continue;
            }
            if (recConversation != null && recConversation.getIsShield() == 1) {
                // 该用户已被对方屏蔽
                continue;
            }
            ConversationDo unreadUpdate = recvUserMsgNotice(recConversation, chatLog, conversationMap, recCvsId);
            if (unreadUpdate != null) {
                unreadUpdateSet.add(unreadUpdate.getOwnerUserId());
            }
        }

        // 消息入库
        if (collect.size() > 0) {
            messageLogDao.insertOrUpdate(collect);
        }

        // 会话入库
        conversationDao.insertOrUpdate(conversationMap.values());

        // 接收人会话统计更新
        if (unreadUpdateSet.size() == 0) {
            return;
        }
        unreadUpdateSet.forEach(recvUserId -> {
            if (!SessionManager.isOnline(recvUserId)) {
                return;
            }
            List<ConversationDo> conversationDoList = conversationDao.selectList(new LambdaQueryWrapper<ConversationDo>()
                    .eq(ConversationDo::getOwnerUserId, recvUserId)
                    .eq(ConversationDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
            if (!CollectionUtils.isEmpty(conversationDoList)) {
                int unread = conversationDoList.stream()
                        .mapToInt(ConversationDo::getUnreadCount)
                        .sum();
                Map<String, Integer> chatMsgCount = Map.of("chatMsgCount", unread);
                WsMessageDTO<Map<String, Integer>> wsMessageDTO = new WsMessageDTO<>(SysConstant.MSG_TYPE_MESSAGE, SysConstant.GET_UNREAD_COUNT, recvUserId, chatMsgCount);
                SessionManager.sendMessage(wsMessageDTO);
            }
        });

        // 会话更新通知
        List<WsMessageDTO> wsMessageDTOList = new ArrayList<>();
        conversationMap.values().stream()
                .filter(c -> CommonStatusEnum.isNormal(c.getStatus()) && c.getIsNotDisturb() == 0)
                .peek(c -> {
                    WsMessageDTO<Object> messageDTO = new WsMessageDTO<>(c.getOwnerUserId(),
                            SysConstant.MSG_TYPE_SESSION, SysConstant.GET_SESSION_LIST);
                    wsMessageDTOList.add(messageDTO);
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(wsMessageDTOList)) {
            return;
        }
        wsMessageDTOList.forEach(wsMessage -> {
            if (!SessionManager.isOnline(wsMessage)) {
                return;
            }
            SessionManager.sendMessage(messageService.scheduleCallback(wsMessage));
        });
    }

    /**
     * @return
     * @description: 接收人消息通知
     * @param: recConversation
     * @param: chatLog
     * @param: conversationMap
     * @param: recCvsId
     * @author: lovbe0210
     * @date: 2025/3/5 0:37
     */
    private static ConversationDo recvUserMsgNotice(ConversationDo recConversation, ChatMessageLogDo chatLog, Map<String, ConversationDo> conversationMap, String recCvsId) {
        if (recConversation == null) {
            recConversation = new ConversationDo()
                    .setOwnerUserId(chatLog.getRecvId())
                    .setTargetUserId(chatLog.getSendId());
            recConversation.setUid(YitIdHelper.nextId())
                    .setStatus(CommonStatusEnum.NORMAL.getStatus())
                    .setCreateTime(new Date())
                    .setUpdateTime(recConversation.getCreateTime());
            conversationMap.put(recCvsId, recConversation);
        } else if (!CommonStatusEnum.isNormal(recConversation.getStatus())) {
            // 已删除会话，需要指定最小记录id
            recConversation.setMinChatLogSeq(chatLog.getUid())
                    .setUnreadCount(0)
                    .setStatus(CommonStatusEnum.NORMAL.getStatus())
                    .setCreateTime(new Date())
                    .setUpdateTime(recConversation.getCreateTime());
        }
        // 设置最后一条消息
        recConversation.setLastMsgId(chatLog.getUid());
        // 发送消息通知
        if (SessionManager.isOnline(chatLog.getRecvId())) {
            MessageConfirmVo messageVo = new MessageConfirmVo()
                    .setSessionId(recConversation.getUid())
                    .setSendSuccess(1);
            BeanUtil.copyProperties(chatLog, messageVo);
            WsMessageDTO<Object> wsMessageDTO = new WsMessageDTO<>(
                    SysConstant.MSG_TYPE_MESSAGE, SysConstant.RECV_MESSAGE, chatLog.getRecvId(), messageVo);
            SessionManager.sendMessage(wsMessageDTO);
        }
        // 判断是否会话免打扰
        if (recConversation.getIsNotDisturb() == 1) {
            return null;
        }
        // 设置未读数
        recConversation.setUnreadCount(recConversation.getUnreadCount() + 1);
        return recConversation;
    }

    /**
     * @description: 发送人消息确认
     * @param: sendConversation
     * @param: recConversation
     * @param: iterator
     * @param: configMap
     * @param: chatLog
     * @author: lovbe0210
     * @date: 2025/3/4 23:47
     */
    private static boolean sendUserMsgConfirm(ConversationDo sendConversation, ConversationDo recConversation, Map<Long, NoticeConfigDo> configMap, ChatMessageLogDo chatLog) {
        boolean success = false;
        MessageConfirmVo confirmVo = null;
        if (sendConversation.getIsShield() == 1) {
            // 消息发送失败，需要先解除屏蔽
            confirmVo = new MessageConfirmVo()
                    .setSessionId(sendConversation.getUid())
                    .setSendSuccess(0)
                    .setErrorReason("发送失败，对方已被你屏蔽");
        } else if (recConversation != null && recConversation.getIsShield() == 1) {
            // 消息发送失败，已被对方屏蔽，但是这里不加提示语
            confirmVo = new MessageConfirmVo()
                    .setSessionId(sendConversation.getUid())
                    .setSendSuccess(0)
                    .setErrorReason("因对方隐私设置，暂无法发送聊天消息");
        } else if (configMap.get(chatLog.getRecvId()) != null && configMap.get(chatLog.getRecvId()).getEnableChatMessage() == 0) {
            // 消息发送失败，对方已设置不允许别人发起私信消息
            confirmVo = new MessageConfirmVo()
                    .setSessionId(sendConversation.getUid())
                    .setSendSuccess(0)
                    .setErrorReason("对方已设置不允许发送私信消息");
        } else {
            // 消息发送成功
            confirmVo = new MessageConfirmVo()
                    .setSessionId(sendConversation.getUid())
                    .setSendSuccess(1);
            sendConversation.setLastMsgId(chatLog.getUid());
            success = true;
        }
        BeanUtil.copyProperties(chatLog, confirmVo);
        if (SessionManager.isOnline(chatLog.getSendId())) {
            WsMessageDTO<Object> wsMessageDTO = new WsMessageDTO<>(
                    SysConstant.MSG_TYPE_MESSAGE, SysConstant.MESSAGE_CONFIRM, chatLog.getSendId(), confirmVo);
            SessionManager.sendMessage(wsMessageDTO);
        }
        return success;

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
                                              List<LikeActionDo> likeActionUpdateList,
                                              List<Long> likeActionDeleteList,
                                              List<LikeActionDo> statisticAddList,
                                              List<LikeActionDo> statisticSubList,
                                              Set<Long> userIdSet, Set<Long> targetIdSet) {
        LikeActionDo action = list.get(0);
        list.sort((o1, o2) -> o1.getCreateTime().compareTo(o2.getCreateTime()));
        LikeActionDo lastAction = list.get(list.size() - 1);
        if (lastAction.getAction() == 1) {
            // 获取最后一条消息的时间更新点赞时间
            if (actionDB != null) {
                actionDB.setUpdateTime(list.get(list.size() - 1).getCreateTime());
                actionDB.setTargetUserId(lastAction.getTargetUserId());
                likeActionUpdateList.add(actionDB);
            } else {
                lastAction.setUid(YitIdHelper.nextId())
                        .setUpdateTime(lastAction.getCreateTime())
                        .setStatus(CommonStatusEnum.NORMAL.getStatus());
                lastAction.setNewAction(true);
                likeActionUpdateList.add(lastAction);
                statisticAddList.add(lastAction);
                userIdSet.add(action.getUserId());
                if (action.getTargetType() < 4) {
                    targetIdSet.add(action.getTargetId());
                }
            }
        } else if (actionDB != null && lastAction.getAction() == 0) {
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
                actionDB.setTargetUserId(action.getTargetUserId());
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
                action.setNewAction(true);
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
