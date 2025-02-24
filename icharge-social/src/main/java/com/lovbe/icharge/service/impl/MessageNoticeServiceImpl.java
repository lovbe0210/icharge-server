package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.vo.PublicArticleVo;
import com.lovbe.icharge.common.model.vo.RamblyJotVo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.dao.NoticeConfigDao;
import com.lovbe.icharge.dao.ReplyCommentDao;
import com.lovbe.icharge.dao.SocialNoticeDao;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.CommentNoticeVo;
import com.lovbe.icharge.service.MessageNoticeService;
import com.lovbe.icharge.service.feign.ContentPickService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/23 16:09
 * @Description: MS
 */
@Service
public class MessageNoticeServiceImpl implements MessageNoticeService {
    @Resource
    private NoticeConfigDao noticeConfigDao;
    @Resource
    private SocialNoticeDao socialNoticeDao;
    @Resource
    private ReplyCommentDao replyCommentDao;
    @Resource
    private ContentPickService cpsService;
    @Resource
    private CommonService commonService;

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

    @Override
    public PageBean<CommentNoticeVo> getCommentNotice(SocialNoticeReqDTO data, Long userId) {
        Long count = socialNoticeDao.selectCount(new LambdaQueryWrapper<SocialNoticeDo>()
                .eq(SocialNoticeDo::getNoticeType, SysConstant.NOTICE_COMMENT)
                .eq(SocialNoticeDo::getUserId, userId)
                .eq(data.getReadStatus() != null, SocialNoticeDo::getReadStatus, data.getReadStatus())
                .orderByDesc(SocialNoticeDo::getCreateTime));
        if (count == null || count == 0) {
            return new PageBean<>(0, List.of());
        }
        List<SocialNoticeDo> socialNoticeList = socialNoticeDao.selectCommentListCount(data, userId);
        if (CollectionUtils.isEmpty(socialNoticeList)) {
            return new PageBean<>(Math.toIntExact(count), List.of());
        }
        Map<Long, PublicArticleVo> articleVoMap = new HashMap<>();
        Map<Long, RamblyJotVo> ramblyJotVoMap = new HashMap<>();
        socialNoticeList.stream()
                .collect(Collectors.groupingBy(SocialNoticeDo::getTargetType,
                        Collectors.mapping(SocialNoticeDo::getTargetId, Collectors.toSet())))
                .forEach((type, idList) -> {
                    switch (type) {
                        case SysConstant.TARGET_TYPE_ARTICLE -> {
                            // 文章类型
                            ResponseBean<List<PublicArticleVo>> articleList= cpsService.getArticleListByIds(new BaseRequest<>(new ArrayList<>(idList)), userId);
                            if (articleList.isResult() && !CollectionUtils.isEmpty(articleList.getData())) {
                                articleVoMap.putAll(articleList.getData().stream()
                                        .collect(Collectors.toMap(PublicArticleVo::getUid, Function.identity())));
                            }
                        }
                        case SysConstant.TARGET_TYPE_ESSAY -> {
                            // 随笔类型
                            ResponseBean<List<RamblyJotVo>> listByIds = cpsService.getRamblyjotListByIds(new BaseRequest<>(new ArrayList<>(idList)), userId);
                            if (listByIds.isResult() && !CollectionUtils.isEmpty(listByIds.getData())) {
                                ramblyJotVoMap.putAll(listByIds.getData().stream()
                                        .collect(Collectors.toMap(RamblyJotVo::getUid, Function.identity())));
                            }
                        }
                    }
                });
        // 组装文章和随笔信息
        List<CommentNoticeVo> collect = socialNoticeList.stream()
                .map(socialNotice -> {
                    CommentNoticeVo commentNotice = new CommentNoticeVo();
                    BeanUtil.copyProperties(socialNotice, commentNotice);
                    commentNotice.setActionUserInfo(commonService.getCacheUser(socialNotice.getActionUserId()));
                    if (Objects.equals(socialNotice.getTargetType(), SysConstant.TARGET_TYPE_ARTICLE)) {
                        commentNotice.setArticleInfo(articleVoMap.get(socialNotice.getTargetId()));
                    }
                    if (Objects.equals(socialNotice.getTargetType(), SysConstant.TARGET_TYPE_ESSAY)) {
                        commentNotice.setRamblyJot(ramblyJotVoMap.get(socialNotice.getTargetId()));
                    }
                    if (socialNotice.getReplyId() != null) {
                        // 获取回复内容
                        ReplyCommentDo replyComment = replyCommentDao.selectById(socialNotice.getReplyId());
                        if (replyComment == null) {
                            return null;
                        }
                        commentNotice.setReplyContent(replyComment.getContent());
                    }
                    return commentNotice;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new PageBean<>(Math.toIntExact(count), collect);
    }
}
