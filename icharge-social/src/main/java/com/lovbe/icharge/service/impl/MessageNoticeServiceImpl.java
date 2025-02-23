package com.lovbe.icharge.service.impl;

import com.lovbe.icharge.dao.NoticeConfigDao;
import com.lovbe.icharge.entity.dto.NoticeConfigDTO;
import com.lovbe.icharge.entity.dto.NoticeConfigDo;
import com.lovbe.icharge.service.MessageNoticeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/23 16:09
 * @Description: MS
 */
@Service
public class MessageNoticeServiceImpl implements MessageNoticeService {
    @Resource
    private NoticeConfigDao noticeConfigDao;
    @Override
    public NoticeConfigDo getNoticeSetting(Long userId) {
        NoticeConfigDo noticeConfigDo = noticeConfigDao.selectById(userId);
        if (noticeConfigDo == null) {
            noticeConfigDo = new NoticeConfigDo(true);
            noticeConfigDo.setUid(userId);
            noticeConfigDao.insertOrUpdate(noticeConfigDo);
        }
        return noticeConfigDo;
    }

    @Override
    public void updateNoticeSetting(NoticeConfigDTO data, Long userId) {
        NoticeConfigDo noticeConfigDo = noticeConfigDao.selectById(userId);
        if (noticeConfigDo == null) {
            noticeConfigDo = new NoticeConfigDo(true);
        }
        if (data.getNewMsgDot() != null) {
            noticeConfigDo.setNewMsgDot(data.getNewMsgDot());
        }
        if (data.getMsgCount() != null) {
            noticeConfigDo.setMsgCount(data.getMsgCount());
        }
        if (data.getCommentMsgAccept() != null) {
            noticeConfigDo.setCommentMsgAccept(data.getCommentMsgAccept());
        }
        if (data.getLikeMsgAccept() != null) {
            noticeConfigDo.setLikeMsgAccept(data.getLikeMsgAccept());
        }
        if (data.getNewFollowerMsg() != null) {
            noticeConfigDo.setNewFollowerMsg(data.getNewFollowerMsg());
        }
        if (data.getSystemNotice() != null) {
            noticeConfigDo.setSystemNotice(data.getSystemNotice());
        }
        if (data.getEnableChatMessage() != null) {
            noticeConfigDo.setEnableChatMessage(data.getEnableChatMessage());
        }
        noticeConfigDao.insertOrUpdate(noticeConfigDo);
    }
}
