package com.lovbe.icharge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.dao.ConversationDao;
import com.lovbe.icharge.dao.SocialNoticeDao;
import com.lovbe.icharge.entity.dto.ConversationDo;
import com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo;
import com.lovbe.icharge.service.ChatMessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/23 23:58
 * @Description: MS
 */
@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    @Resource
    private SocialNoticeDao socialNoticeDao;
    @Resource
    private ConversationDao conversationDao;

    @Override
    public UnreadMsgStatisticVo getUnreadStatistic(Long userId) {
        // 获取未读通知明细表的分组统计数据
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
    }
}
