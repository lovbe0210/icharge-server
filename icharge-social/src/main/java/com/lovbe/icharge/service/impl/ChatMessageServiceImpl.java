package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.dto.RelationshipDo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.config.SessionManager;
import com.lovbe.icharge.dao.ChatMessageLogDao;
import com.lovbe.icharge.dao.ConversationDao;
import com.lovbe.icharge.dao.SocialNoticeDao;
import com.lovbe.icharge.entity.dto.ChatMessageLogDo;
import com.lovbe.icharge.entity.dto.ConversationDTO;
import com.lovbe.icharge.entity.dto.ConversationDo;
import com.lovbe.icharge.entity.dto.WsMessageDTO;
import com.lovbe.icharge.entity.vo.ChatMessageVo;
import com.lovbe.icharge.entity.vo.MessageSessionVo;
import com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo;
import com.lovbe.icharge.service.ChatMessageService;
import com.lovbe.icharge.service.UserSocialService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

                        sessionVo.setSessionId(conversation.getUid())
                                .setOwnerUserId(conversation.getOwnerUserId())
                                .setSessionUserInfo(commonService.getCacheUser(conversation.getTargetUserId()))
                                .setSessionTime(conversation.getCreateTime());
                        ChatMessageLogDo messageLog = messageLogMap.get(conversation.getLastMsgId());
                        if (messageLog == null) {
                            return sessionVo;
                        }
                        ChatMessageVo messageVo = new ChatMessageVo();
                        BeanUtil.copyProperties(messageLog, messageVo);
                        messageVo.setServerMsgId(messageLog.getUid());
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
            return null;
        }
        try {
            switch (callback) {
                case SysConstant.GET_SESSION_LIST -> {
                    List<MessageSessionVo> sessionList = this.getSessionList(wsMessageDTO.getUserId());
                    String jsonString = JsonUtils.toJsonString(sessionList);
                    String msgBody = CommonUtils.bitwiseInvert(Base64.encode(jsonString));
                    wsMessageDTO.setData(msgBody);
                    return wsMessageDTO;
                }
                case SysConstant.SEND_MESSAGE -> {
                    Object data = wsMessageDTO.getData();
                    String decoded = Base64.decodeStr(CommonUtils.bitwiseInvert((String) data));
                    ChatMessageLogDo chatMessageLogDo = JsonUtils.parseObject(decoded, ChatMessageLogDo.class);

                    log.error(chatMessageLogDo.toString());
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
            conversationDo.setUid(YitIdHelper.nextId())
                    .setStatus(CommonStatusEnum.NORMAL.getStatus());
        } else if (CommonStatusEnum.isNormal(conversationDo.getStatus())) {
            // 判断会话状态，如果是已删除会话，则需要设置最小会话记录id
            ChatMessageLogDo messageLogDo = messageLogDao.selectOne(new LambdaQueryWrapper<ChatMessageLogDo>()
                    .eq(ChatMessageLogDo::getSendId, userId)
                    .eq(ChatMessageLogDo::getRecvId, data.getTargetUserId())
                    .or()
                    .eq(ChatMessageLogDo::getRecvId, userId)
                    .eq(ChatMessageLogDo::getSendId, data.getTargetUserId())
                    .orderByDesc(ChatMessageLogDo::getSendTime)
                    .last("limit 1"));
            if (messageLogDo != null) {
                conversationDo.setMinChatLogSeq(messageLogDo.getUid());
            }
        }
        // 更新创建时间，将会话排到前面
        conversationDo.setCreateTime(new Date()).setUpdateTime(conversationDo.getCreateTime());;
        conversationDao.insertOrUpdate(conversationDo);
        // 发送通知会话列表更新了
        WsMessageDTO<Object> messageDTO = new WsMessageDTO<>(userId, SysConstant.MSG_TYPE_SESSION, SysConstant.GET_SESSION_LIST);
        SessionManager.sendMessage(scheduleCallback(messageDTO));
        return conversationDo.getUid();
    }
}
