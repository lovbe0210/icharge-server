package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.dto.RelationshipDo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.config.SessionManager;
import com.lovbe.icharge.dao.ChatMessageLogDao;
import com.lovbe.icharge.dao.ConversationDao;
import com.lovbe.icharge.dao.SocialNoticeDao;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.MessageActionVo;
import com.lovbe.icharge.entity.vo.MessageSessionVo;
import com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo;
import com.lovbe.icharge.service.ChatMessageService;
import com.lovbe.icharge.service.UserSocialService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/23 23:58
 * @Description: 注意这里的接口都可以通过ws进行调用，因此需要控制异常处理
 */
@Slf4j
@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    @Resource
    private SocialNoticeDao socialNoticeDao;
    @Resource
    private ConversationDao conversationDao;
    @Resource
    private ChatMessageLogDao messageLogDao;

    @Resource
    private CommonService commonService;
    @Resource
    private UserSocialService socialService;

    @Value("${spring.kafka.topics.chat-send-message}")
    private String sendMessageTopic;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public UnreadMsgStatisticVo getUnreadStatistic(Long userId) {
        // 获取未读通知明细表的分组统计数据
        try {
            UnreadMsgStatisticVo unreadMsgStatistic = socialNoticeDao.selectUnreadStatistic(userId);
            // 获取所有会话进行未读统计
            List<ConversationDo> conversationList = conversationDao.selectList(new LambdaQueryWrapper<ConversationDo>()
                    .eq(ConversationDo::getOwnerUserId, userId)
                    .eq(ConversationDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
            if (!CollectionUtils.isEmpty(conversationList)) {
                int unread = conversationList.stream()
                        .mapToInt(ConversationDo::getUnreadCount)
                        .sum();
                unreadMsgStatistic.setChatMsgCount(unread);
            }
            return unreadMsgStatistic;
        } catch (Exception e) {
            log.error("[消息会话] --- 获取未读消息统计，errorInfo: {}", e.toString());
            return new UnreadMsgStatisticVo();
        }
    }

    @Override
    public List<MessageSessionVo> getSessionList(Long userId) {
        try {
            List<ConversationDo> conversationList = conversationDao.selectList(new LambdaQueryWrapper<ConversationDo>()
                    .eq(ConversationDo::getOwnerUserId, userId)
                    .eq(ConversationDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
            if (CollectionUtils.isEmpty(conversationList)) {
                return List.of();
            }
            Set<Long> lastMsgIds = conversationList.stream()
                    .map(ConversationDo::getLastMsgId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            List<MessageSessionVo> sessionList = List.of();
            Map<Long, ChatMessageLogDo> messageLogMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(lastMsgIds)) {
                List<ChatMessageLogDo> messageLogList = messageLogDao.selectBatchIds(lastMsgIds);
                if (!CollectionUtils.isEmpty(messageLogList)) {
                    messageLogMap.putAll(messageLogList.stream()
                            .collect(Collectors.toMap(ChatMessageLogDo::getUid, Function.identity())));
                }
            }
            sessionList = conversationList.stream()
                    .map(conversation -> {
                        MessageSessionVo sessionVo = new MessageSessionVo();
                        BeanUtil.copyProperties(conversation, sessionVo);
                        sessionVo.setUid(conversation.getUid())
                                .setOwnerUserId(conversation.getOwnerUserId())
                                .setSessionUserInfo(commonService.getCacheUser(conversation.getTargetUserId()))
                                .setSessionTime(conversation.getCreateTime());
                        ChatMessageLogDo messageLog = messageLogMap.get(conversation.getLastMsgId());
                        if (messageLog == null) {
                            return sessionVo;
                        }
                        MessageConfirmVo messageVo = new MessageConfirmVo();
                        BeanUtil.copyProperties(messageLog, messageVo);
                        if (Objects.equals(SysConstant.MESSAGE_ROLLBACK, messageVo.getContentType())) {
                            messageVo.setContent("撤回了一条消息");
                        }
                        sessionVo.setLastMsg(messageVo)
                                .setSessionTime(messageLog.getSendTime());
                        return sessionVo;
                    })
                    .sorted((s1, s2) -> {
                        Integer isPinned1 = s1.getIsPinned();
                        Integer isPinned2 = s2.getIsPinned();
                        if (isPinned1 == isPinned2) {
                            return s2.getSessionTime().compareTo(s1.getSessionTime());
                        } else {
                            return isPinned2 - isPinned1;
                        }
                    })
                    .collect(Collectors.toList());
            return sessionList;
        } catch (Exception e) {
            log.error("[消息会话] --- 获取消息会话列表，errorInfo: {}", e.toString());
            return List.of();
        }
    }

    @Override
    public WsMessageDTO<Object> scheduleCallback(WsMessageDTO wsMessageDTO) {
        String callback = wsMessageDTO.getCallback();
        if (!StringUtils.hasText(callback)) {
            log.error("[ws回调消息处理] --- callback is null return null");
            return null;
        }
        if (wsMessageDTO.getUserId() == null) {
            log.error("[ws回调消息处理] --- userId is null return null");
            return null;
        }
        try {
            switch (callback) {
                case SysConstant.GET_SESSION_LIST -> {
                    List<MessageSessionVo> sessionList = this.getSessionList(wsMessageDTO.getUserId());
                    wsMessageDTO.setData(sessionList);
                    return wsMessageDTO;
                }
                case SysConstant.GET_CHAT_LOGS -> {
                    Map param = wsMessageDTO.getParam();
                    Long sessionId = MapUtil.getLong(param, SysConstant.SESSION_ID);
                    if (sessionId == null) {
                        log.error("[ws获取聊天记录] --- sessionId is null return null");
                        return null;
                    }
                    int offset = MapUtil.getInt(param, SysConstant.OFFSET, 0);
                    int limit = MapUtil.getInt(param, SysConstant.LIMIT, 20);
                    List<MessageConfirmVo> chatLogList = this.getChatLogList(wsMessageDTO.getUserId(), sessionId, offset, limit);
                    Map<String, Object> chatLogObject = Map.of(SysConstant.SESSION_ID, sessionId, SysConstant.LIST, chatLogList);
                    wsMessageDTO.setData(chatLogObject);
                    return wsMessageDTO;
                }
                case SysConstant.SEND_MESSAGE -> {
                    Object data = wsMessageDTO.getData();
                    String decoded = Base64.decodeStr(CommonUtils.bitwiseInvert((String) data));
                    ChatMessageLogDo chatMessageLogDo = JsonUtils.parseObject(decoded, ChatMessageLogDo.class);
                    chatMessageLogDo.setUid(YitIdHelper.nextId())
                            .setStatus(CommonStatusEnum.NORMAL.getStatus())
                            .setCreateTime(new Date())
                            .setUpdateTime(chatMessageLogDo.getCreateTime());
                    chatMessageLogDo.setReadStatus(0)
                            .setSendTime(chatMessageLogDo.getCreateTime());
                    commonService.sendMessage(appName, sendMessageTopic, chatMessageLogDo);
                    return null;
                }
                case SysConstant.DELETE_MESSAGE -> {
                    Long messageId = (Long) wsMessageDTO.getData();
                    if (messageId == null) {
                        log.error("[ws删除消息] --- messageId is null");
                    } else {
                        this.deleteMessageLog(messageId, wsMessageDTO.getUserId());
                    }
                    return null;
                }

                case SysConstant.ROLLBACK_MESSAGE -> {
                    Long messageId = (Long) wsMessageDTO.getData();
                    if (messageId == null) {
                        log.error("[ws撤回消息] --- messageId is null");
                    } else {
                        MessageActionVo messageAction = this.rollbackMessageLog(messageId, wsMessageDTO.getUserId());
                        // 成功时回调所有连接，失败时只回调当前连接
                        if (messageAction != null && !messageAction.isResult()) {
                            wsMessageDTO.setData(messageAction);
                            return wsMessageDTO;
                        }
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("[[ws消息回执] --- 消息解析/组装失败，errorInfo: {}", e.toString());
        }
        return null;
    }

    @Override
    public Long startMessageSession(ConversationDTO data, Long userId) {
        // 先通过userId和targetUserId判断是否已存在会话
        ConversationDo conversationDo = conversationDao.selectOne(new LambdaQueryWrapper<ConversationDo>()
                .eq(ConversationDo::getOwnerUserId, userId)
                .eq(ConversationDo::getTargetUserId, data.getTargetUserId()), false);
        // 如果会话为空，说明是新对话
        if (conversationDo == null) {
            conversationDo = new ConversationDo()
                    .setOwnerUserId(userId)
                    .setTargetUserId(data.getTargetUserId());
            // 获取会话状态，判断是否为单向会话
            RelationshipDo relationship = socialService.getRelationship(data.getTargetUserId(), userId);
            if (relationship == null) {
                conversationDo.setIsOneWay(1);
            } else if ((Objects.equals(relationship.getUserIdMaster(), data.getTargetUserId())
                    && Objects.equals(relationship.getMasterWatchSlave(), 0))
                    || (Objects.equals(relationship.getUserIdSlave(), data.getTargetUserId())
                    && Objects.equals(relationship.getMasterWatchSlave(), 0))) {
                conversationDo.setIsOneWay(1);
            }
            conversationDo.setUid(YitIdHelper.nextId());
        } else if (!CommonStatusEnum.isNormal(conversationDo.getStatus())) {
            // 判断会话状态，如果是已删除会话，则需要设置最小会话记录id
            ChatMessageLogDo messageLogDo = messageLogDao.selectOne(new LambdaQueryWrapper<ChatMessageLogDo>()
                    .eq(ChatMessageLogDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                    .and(wrap -> wrap.eq(ChatMessageLogDo::getSendId, userId).eq(ChatMessageLogDo::getRecvId, data.getTargetUserId())
                            .or(wp -> wp.eq(ChatMessageLogDo::getSendId, data.getTargetUserId()).eq(ChatMessageLogDo::getRecvId, userId))
                    )
                    .orderByDesc(ChatMessageLogDo::getSendTime)
                    .last("limit 1"));
            if (messageLogDo != null) {
                conversationDo.setMinChatLogSeq(messageLogDo.getUid());
            }
        }
        // 更新创建时间，将会话排到前面
        conversationDo.setCreateTime(new Date())
                .setUpdateTime(conversationDo.getCreateTime())
                .setStatus(CommonStatusEnum.NORMAL.getStatus());
        conversationDao.insertOrUpdate(conversationDo);
        return conversationDo.getUid();
    }

    public List<MessageConfirmVo> getChatLogList(Long userId, Long sessionId, int offset, int limit) {
        ConversationDo conversation = conversationDao.selectById(sessionId);
        // 会话状态校验
        if (conversation == null || !CommonStatusEnum.isNormal(conversation.getStatus())) {
            log.error("[获取聊天记录] --- 会话已被删除或不存在，无法获取聊天记录");
            return List.of();
        }
        // 会话权限校验
        if (!Objects.equals(userId, conversation.getOwnerUserId())) {
            log.error("[获取聊天记录] --- 当前用户和会话所属人不符，获取失败");
            return List.of();
        }
        if (conversation.getUnreadCount() > 0) {
            // 更新已读数
            conversationDao.update(new UpdateWrapper<ConversationDo>()
                    .eq(SysConstant.ES_FILED_UID, conversation.getUid())
                    .set("unread_count", 0));
            // 获取所有会话进行未读统计
            if (SessionManager.isOnline(userId)) {
                List<ConversationDo> conversationList = conversationDao.selectList(new LambdaQueryWrapper<ConversationDo>()
                        .eq(ConversationDo::getOwnerUserId, userId)
                        .eq(ConversationDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
                if (!CollectionUtils.isEmpty(conversationList)) {
                    int unread = conversationList.stream()
                            .mapToInt(ConversationDo::getUnreadCount)
                            .sum();
                    Map<String, Integer> chatMsgCount = Map.of("chatMsgCount", unread);
                    WsMessageDTO<Map<String, Integer>> wsMessageDTO = new WsMessageDTO<>(SysConstant.MSG_TYPE_MESSAGE, SysConstant.GET_UNREAD_COUNT, userId, chatMsgCount);
                    SessionManager.sendMessage(wsMessageDTO);
                }
            }
        }
        List<ChatMessageLogDo> chatLogList = messageLogDao.selectList(new LambdaQueryWrapper<ChatMessageLogDo>()
                .eq(ChatMessageLogDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .ge(conversation.getMinChatLogSeq() != null, ChatMessageLogDo::getUid, conversation.getMinChatLogSeq())
                .and(wrap -> wrap.eq(ChatMessageLogDo::getSendId, userId)
                        .eq(ChatMessageLogDo::getRecvId, conversation.getTargetUserId())
                        .eq(ChatMessageLogDo::getSendUserDelete, 0)
                        .or(wp -> wp.eq(ChatMessageLogDo::getSendId, conversation.getTargetUserId())
                                .eq(ChatMessageLogDo::getRecvId, userId)
                                .eq(ChatMessageLogDo::getRecvUserDelete, 0))
                )
                .orderByDesc(ChatMessageLogDo::getSendTime)
                .last(" limit " + offset + "," + limit)
        );
        if (CollectionUtils.isEmpty(chatLogList)) {
            return List.of();
        }
        List<MessageConfirmVo> messageVoList = chatLogList.stream()
                .map(chatLog -> {
                    MessageConfirmVo messageVo = new MessageConfirmVo();
                    BeanUtil.copyProperties(chatLog, messageVo);
                    messageVo.setSessionId(sessionId);
                    messageVo.setSendSuccess(1);
                    if (Objects.equals(SysConstant.MESSAGE_ROLLBACK, messageVo.getContentType())) {
                        messageVo.setContent("撤回了一条消息");
                    }
                    return messageVo;
                })
                .collect(Collectors.toList());
        return messageVoList;
    }

    @Override
    public void updateMessageSession(ConversationUpdateDTO data, Long userId) {
        ConversationDo conversationDo = conversationDao.selectById(data.getSessionId());
        if (conversationDo == null || !CommonStatusEnum.isNormal(conversationDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.CHAT_CONVERSATION_NOT_EXIST);
        }
        if (!Objects.equals(conversationDo.getOwnerUserId(), userId)) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        if (data.getIsNotDisturb() != null) {
            conversationDo.setIsNotDisturb(data.getIsNotDisturb());
        }
        if (data.getIsPinned() != null) {
            conversationDo.setIsPinned(data.getIsPinned());
            conversationDao.update(new UpdateWrapper<ConversationDo>()
                    .eq("owner_user_id", userId)
                    .set("is_pinned", 0));
        }
        if (data.getIsShield() != null) {
            conversationDo.setIsShield(data.getIsShield());
        }
        if (data.getUnreadCount() != null) {
            conversationDo.setUnreadCount(data.getUnreadCount());
        }
        conversationDao.updateById(conversationDo);
        // 如果是置顶会话需要更新会话列表
        if (data.getIsPinned() != null && SessionManager.isOnline(userId)) {
            List<MessageSessionVo> sessionList = this.getSessionList(userId);
            WsMessageDTO<List<MessageSessionVo>> wsMessageDTO = new WsMessageDTO<>(
                    SysConstant.MSG_TYPE_SESSION, SysConstant.GET_SESSION_LIST, null, userId, sessionList);
            SessionManager.sendMessage(wsMessageDTO);
        }
    }

    @Override
    public void deleteMessageSession(ConversationUpdateDTO data, Long userId) {
        ConversationDo conversationDo = conversationDao.selectById(data.getSessionId());
        if (conversationDo == null || !CommonStatusEnum.isNormal(conversationDo.getStatus())) {
            return;
        }
        if (!Objects.equals(conversationDo.getOwnerUserId(), userId)) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        conversationDo.setLastMsgId(null)
                .setUnreadCount(0)
                .setStatus(CommonStatusEnum.DELETE.getStatus());
        conversationDao.update(new UpdateWrapper<ConversationDo>()
                .eq(SysConstant.ES_FILED_UID, conversationDo.getUid())
                .set("status", "D")
                .set("last_msg_id", null)
                .set("unread_count", 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageActionVo deleteMessageLog(Long messageId, Long userId) {
        MessageActionVo messageActionVo = new MessageActionVo().setMessageId(messageId).setResult(true);
        ChatMessageLogDo messageLogDo = messageLogDao.selectById(messageId);
        if (messageLogDo == null || CommonStatusEnum.isDelete(messageLogDo.getStatus())) {
            // 消息删除通知
            SessionManager.sendMessage(new WsMessageDTO(SysConstant.MSG_TYPE_MESSAGE, SysConstant.DELETE_MESSAGE, null,
                    userId, messageActionVo));
            return messageActionVo;
        }
        Long sendId = messageLogDo.getSendId();
        Long recvId = messageLogDo.getRecvId();
        messageLogDao.update(new UpdateWrapper<ChatMessageLogDo>()
                .eq(SysConstant.ES_FILED_UID, messageId)
                .set(Objects.equals(userId, sendId), "send_user_delete", 1)
                .set(Objects.equals(userId, recvId), "recv_user_delete", 1));
        Long targetSessionUser = Objects.equals(sendId, userId) ? recvId : sendId;
        ConversationDo conversationDo = conversationDao.selectOne(new LambdaQueryWrapper<ConversationDo>()
                .eq(ConversationDo::getOwnerUserId, userId)
                .eq(ConversationDo::getTargetUserId, targetSessionUser)
                .eq(ConversationDo::getLastMsgId, messageId)
                .eq(ConversationDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
        if (conversationDo != null) {
            // 获取最后一条消息设置为lastMsgId
            ChatMessageLogDo lastMsg = messageLogDao.selectOne(new LambdaQueryWrapper<ChatMessageLogDo>()
                    .eq(ChatMessageLogDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                    .and(wrap -> wrap.eq(ChatMessageLogDo::getSendId, userId)
                                     .eq(ChatMessageLogDo::getRecvId, targetSessionUser)
                                     .eq(ChatMessageLogDo::getSendUserDelete, 0)
                            .or(wp -> wp.eq(ChatMessageLogDo::getSendId, targetSessionUser)
                                    .eq(ChatMessageLogDo::getRecvId, userId)
                                    .eq(ChatMessageLogDo::getRecvUserDelete, 0))
                    )
                    .orderByDesc(ChatMessageLogDo::getSendTime)
                    .last(" limit 1"));
            if (lastMsg != null) {
                conversationDao.update(new UpdateWrapper<ConversationDo>()
                        .eq(SysConstant.ES_FILED_UID, conversationDo.getUid())
                        .set("last_msg_id", lastMsg.getUid()));
                // 会话更新通知
                if (SessionManager.isOnline(userId)) {
                    List<MessageSessionVo> sessionList = this.getSessionList(userId);
                    SessionManager.sendMessage(new WsMessageDTO(SysConstant.MSG_TYPE_SESSION, SysConstant.GET_SESSION_LIST, null,
                            userId, sessionList));
                }
            }
        }
        // 消息删除通知
        SessionManager.sendMessage(new WsMessageDTO(SysConstant.MSG_TYPE_MESSAGE, SysConstant.DELETE_MESSAGE, null,
                userId, messageActionVo));
        return messageActionVo;
    }

    @Override
    public MessageActionVo rollbackMessageLog(Long messageId, Long userId) {
        ChatMessageLogDo messageLogDo = messageLogDao.selectById(messageId);
        if (messageLogDo == null || CommonStatusEnum.isDelete(messageLogDo.getStatus())) {
            return new MessageActionVo().setMessageId(messageId)
                    .setResult(false)
                    .setReason("消息不存在或已被删除");
        }
        if (!Objects.equals(userId, messageLogDo.getSendId())) {
            return new MessageActionVo().setMessageId(messageId)
                    .setResult(false)
                    .setReason("只能撤回自己发送的消息");
        }
        Date sendTime = messageLogDo.getSendTime();
        if (sendTime == null || (System.currentTimeMillis() - sendTime.getTime() > 60 * 2 * 1000)) {
            return new MessageActionVo().setMessageId(messageId)
                    .setResult(false)
                    .setReason("只能撤回两分钟内发的消息");
        }
        messageLogDao.update(new UpdateWrapper<ChatMessageLogDo>()
                .eq(SysConstant.ES_FILED_UID, messageId)
                .set("content_type", SysConstant.MESSAGE_ROLLBACK));
        // 消息撤回通知
        MessageActionVo messageActionVo = new MessageActionVo().setMessageId(messageId).setResult(true);
        SessionManager.sendMessage(new WsMessageDTO(SysConstant.MSG_TYPE_MESSAGE, SysConstant.ROLLBACK_MESSAGE, null,
                userId, messageActionVo));
        SessionManager.sendMessage(new WsMessageDTO(SysConstant.MSG_TYPE_MESSAGE, SysConstant.ROLLBACK_MESSAGE, null,
                messageLogDo.getRecvId(), messageActionVo));
        // 判断是否是会话的最后一条
        List<ConversationDo> conversationList = conversationDao.selectList(new LambdaQueryWrapper<ConversationDo>()
                .eq(ConversationDo::getLastMsgId, messageLogDo.getUid())
                .eq(ConversationDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .and(wrap -> wrap.eq(ConversationDo::getOwnerUserId, userId).eq(ConversationDo::getTargetUserId, messageLogDo.getRecvId())
                        .or(wp -> wp.eq(ConversationDo::getOwnerUserId, messageLogDo.getRecvId()).eq(ConversationDo::getTargetUserId, userId))
                ));
        if (!CollectionUtils.isEmpty(conversationList)) {
            for (ConversationDo conversationDo : conversationList) {
                List<MessageSessionVo> sessionList = this.getSessionList(conversationDo.getOwnerUserId());
                SessionManager.sendMessage(new WsMessageDTO(SysConstant.MSG_TYPE_SESSION, SysConstant.GET_SESSION_LIST, null,
                        conversationDo.getOwnerUserId(), sessionList));
            }
        }
        return new MessageActionVo().setMessageId(messageId)
                .setResult(true);
    }
}
