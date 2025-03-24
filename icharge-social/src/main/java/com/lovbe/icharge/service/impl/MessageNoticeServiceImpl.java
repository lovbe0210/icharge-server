package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.SocialNoticeDo;
import com.lovbe.icharge.common.model.vo.PublicArticleVo;
import com.lovbe.icharge.common.model.vo.RamblyJotVo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.dao.NoticeConfigDao;
import com.lovbe.icharge.dao.ReplyCommentDao;
import com.lovbe.icharge.dao.SocialNoticeDao;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.CommentNoticeVo;
import com.lovbe.icharge.entity.vo.LikeNoticeVo;
import com.lovbe.icharge.entity.vo.SocialNoticeVo;
import com.lovbe.icharge.entity.vo.SystemNoticeVo;
import com.lovbe.icharge.service.MessageNoticeService;
import com.lovbe.icharge.service.feign.ContentPickService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
                .in(SocialNoticeDo::getNoticeType, SysConstant.NOTICE_COMMENT, SysConstant.NOTICE_REPLY)
                .eq(SocialNoticeDo::getUserId, userId)
                .eq(data.getReadStatus() != null, SocialNoticeDo::getReadStatus, data.getReadStatus()));
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
                            ResponseBean<List<PublicArticleVo>> articleList = cpsService.getArticleListByIds(new BaseRequest<>(idList), userId);
                            if (articleList.isResult() && !CollectionUtils.isEmpty(articleList.getData())) {
                                articleVoMap.putAll(articleList.getData().stream()
                                        .collect(Collectors.toMap(PublicArticleVo::getUid, Function.identity())));
                            }
                        }
                        case SysConstant.TARGET_TYPE_ESSAY -> {
                            // 随笔类型
                            ResponseBean<List<RamblyJotVo>> listByIds = cpsService.getRamblyjotListByIds(new BaseRequest<>(idList), userId);
                            if (listByIds.isResult() && !CollectionUtils.isEmpty(listByIds.getData())) {
                                ramblyJotVoMap.putAll(listByIds.getData().stream()
                                        .collect(Collectors.toMap(RamblyJotVo::getUid, Function.identity())));
                            }
                        }
                    }
                });
        // 组装文章和随笔信息
        List<Long> repltyCommentIds = new ArrayList<>();
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
                .peek(notice -> repltyCommentIds.add(notice.getUid()))
                .collect(Collectors.toList());
        socialNoticeDao.update(new UpdateWrapper<SocialNoticeDo>()
                .in(SysConstant.ES_FILED_UID, repltyCommentIds)
                .set("read_status", 1));
        return new PageBean<>(Math.toIntExact(count), collect);
    }

    @Override
    public PageBean<LikeNoticeVo> getLikesNotice(SocialNoticeReqDTO data, Long userId) {
        Long count = socialNoticeDao.selectCount(new LambdaQueryWrapper<SocialNoticeDo>()
                .eq(SocialNoticeDo::getNoticeType, SysConstant.NOTICE_LIKE)
                .eq(SocialNoticeDo::getUserId, userId)
                .eq(data.getReadStatus() != null, SocialNoticeDo::getReadStatus, data.getReadStatus()));
        if (count == null || count == 0) {
            return new PageBean<>(0, List.of());
        }
        List<SocialNoticeDo> socialNoticeList = socialNoticeDao.selectLikeListCount(data, userId);
        if (CollectionUtils.isEmpty(socialNoticeList)) {
            return new PageBean<>(Math.toIntExact(count), List.of());
        }
        Map<Long, PublicArticleVo> articleVoMap = new HashMap<>();
        Map<Long, RamblyJotVo> ramblyJotVoMap = new HashMap<>();
        Map<Long, SocialNoticeDo> commentDoMap = new HashMap<>();
        Set<Long> articleIds = new HashSet<>();
        Set<Long> ramblyJotIds = new HashSet<>();
        Map<Integer, Set<Long>> collectMap = socialNoticeList.stream()
                .collect(Collectors.groupingBy(SocialNoticeDo::getTargetType,
                        Collectors.mapping(SocialNoticeDo::getTargetId, Collectors.toSet())));
        // 由于点赞里面可能包含评论，而评论的对象又可能是文章或随笔，因此这里先取评论的类型
        // 评论类型
        Set<Long> commentIds = collectMap.get(SysConstant.TARGET_TYPE_COMMENT);
        if (!CollectionUtils.isEmpty(commentIds)) {
            List<SocialNoticeDo> socialNotices = replyCommentDao.selectCommentReplyListByIds(commentIds);
            for (SocialNoticeDo socialNotice : socialNotices) {
                commentDoMap.put(socialNotice.getUid(), socialNotice);
                if (Objects.equals(socialNotice.getTargetType(), SysConstant.TARGET_TYPE_ARTICLE)) {
                    articleIds.add(socialNotice.getTargetId());
                    continue;
                }
                if (Objects.equals(socialNotice.getTargetType(), SysConstant.TARGET_TYPE_ESSAY)) {
                    ramblyJotIds.add(socialNotice.getTargetId());
                }
            }
        }
        // 文章类型
        Set<Long> aIds = collectMap.get(SysConstant.TARGET_TYPE_ARTICLE);
        if (!CollectionUtils.isEmpty(aIds)) {
            articleIds.addAll(aIds);
        }
        if (!CollectionUtils.isEmpty(articleIds)) {
            ResponseBean<List<PublicArticleVo>> articleList = cpsService.getArticleListByIds(new BaseRequest<>(articleIds), userId);
            if (articleList.isResult() && !CollectionUtils.isEmpty(articleList.getData())) {
                articleVoMap.putAll(articleList.getData().stream()
                        .collect(Collectors.toMap(PublicArticleVo::getUid, Function.identity())));
            }
        }
        // 随笔类型
        Set<Long> eIds = collectMap.get(SysConstant.TARGET_TYPE_ESSAY);
        if (!CollectionUtils.isEmpty(eIds)) {
            ramblyJotIds.addAll(eIds);
        }
        ResponseBean<List<RamblyJotVo>> listByIds = cpsService.getRamblyjotListByIds(new BaseRequest<>(ramblyJotIds), userId);
        if (listByIds.isResult() && !CollectionUtils.isEmpty(listByIds.getData())) {
            ramblyJotVoMap.putAll(listByIds.getData().stream()
                    .collect(Collectors.toMap(RamblyJotVo::getUid, Function.identity())));
        }

        // 组装文章、随笔和评论信息
        List<Long> likeIds = new ArrayList<>();
        List<LikeNoticeVo> collect = socialNoticeList.stream()
                .map(socialNotice -> {
                    LikeNoticeVo likeNotice = new LikeNoticeVo();
                    BeanUtil.copyProperties(socialNotice, likeNotice);
                    likeNotice.setActionUserInfo(commonService.getCacheUser(socialNotice.getActionUserId()));
                    if (Objects.equals(socialNotice.getTargetType(), SysConstant.TARGET_TYPE_ARTICLE)) {
                        likeNotice.setArticleInfo(articleVoMap.get(socialNotice.getTargetId()));
                        return likeNotice;
                    }
                    if (Objects.equals(socialNotice.getTargetType(), SysConstant.TARGET_TYPE_ESSAY)) {
                        likeNotice.setRamblyJot(ramblyJotVoMap.get(socialNotice.getTargetId()));
                        return likeNotice;
                    }
                    // 如果是评论补全评论信息
                    if (Objects.equals(socialNotice.getTargetType(), SysConstant.TARGET_TYPE_COMMENT)) {
                        SocialNoticeDo noticeDo = commentDoMap.get(socialNotice.getTargetId());
                        if (noticeDo == null) {
                            return null;
                        }
                        likeNotice.setCommentId(noticeDo.getCommentId())
                                .setCommentContent(noticeDo.getCommentContent())
                                .setReplyId(noticeDo.getReplyId())
                                .setReplyContent(noticeDo.getReplyContent());
                        // 补全评论的target信息
                        if (Objects.equals(noticeDo.getTargetType(), SysConstant.TARGET_TYPE_ARTICLE)) {
                            likeNotice.setArticleInfo(articleVoMap.get(noticeDo.getTargetId()));
                        }
                        if (Objects.equals(noticeDo.getTargetType(), SysConstant.TARGET_TYPE_ESSAY)) {
                            likeNotice.setRamblyJot(ramblyJotVoMap.get(noticeDo.getTargetId()));
                        }
                    }
                    return likeNotice;
                })
                .filter(Objects::nonNull)
                .peek(notice -> likeIds.add(notice.getUid()))
                .collect(Collectors.toList());
        socialNoticeDao.update(new UpdateWrapper<SocialNoticeDo>()
                .in(SysConstant.ES_FILED_UID, likeIds)
                .set("read_status", 1));
        return new PageBean<>(Math.toIntExact(count), collect);
    }

    @Override
    public PageBean<SocialNoticeVo> getFollowsNotice(SocialNoticeReqDTO data, Long userId) {
        Long count = socialNoticeDao.selectCount(new LambdaQueryWrapper<SocialNoticeDo>()
                .eq(SocialNoticeDo::getNoticeType, SysConstant.NOTICE_FOLLOW)
                .eq(SocialNoticeDo::getUserId, userId)
                .eq(data.getReadStatus() != null, SocialNoticeDo::getReadStatus, data.getReadStatus()));
        if (count == null || count == 0) {
            return new PageBean<>(0, List.of());
        }
        List<SocialNoticeDo> noticeDoList = socialNoticeDao.selectList(new LambdaQueryWrapper<SocialNoticeDo>()
                .eq(SocialNoticeDo::getNoticeType, SysConstant.NOTICE_FOLLOW)
                .eq(SocialNoticeDo::getUserId, userId)
                .eq(data.getReadStatus() != null, SocialNoticeDo::getReadStatus, data.getReadStatus())
                .orderByDesc(SocialNoticeDo::getCreateTime)
                .last(" limit " + data.getOffset() + "," + data.getLimit()));
        List<Long> followIds = new ArrayList<>();
        List<SocialNoticeVo> collect = noticeDoList.stream()
                .map(record -> {
                    SocialNoticeVo noticeVo = new SocialNoticeVo();
                    BeanUtil.copyProperties(record, noticeVo);
                    noticeVo.setActionUserInfo(commonService.getCacheUser(record.getActionUserId()));
                    followIds.add(record.getUid());
                    return noticeVo;
                })
                .collect(Collectors.toList());
        socialNoticeDao.update(new UpdateWrapper<SocialNoticeDo>()
                .in(SysConstant.ES_FILED_UID, followIds)
                .set("read_status", 1));
        return new PageBean<>(Math.toIntExact(count), collect);
    }

    @Override
    public PageBean<SystemNoticeVo> getSystemNotice(SocialNoticeReqDTO data, Long userId) {
        Long count = socialNoticeDao.selectCount(new LambdaQueryWrapper<SocialNoticeDo>()
                .in(SocialNoticeDo::getNoticeType, SysConstant.NOTICE_SYSTEM,
                        SysConstant.NOTICE_AUDIT_ARTICLE,
                        SysConstant.NOTICE_AUDIT_EASSAY,
                        SysConstant.NOTICE_AUDIT_COLUMN,
                        SysConstant.NOTICE_AUDIT_DOMAIN)
                .eq(SocialNoticeDo::getUserId, userId)
                .eq(data.getReadStatus() != null, SocialNoticeDo::getReadStatus, data.getReadStatus()));
        if (count == null || count == 0) {
            return new PageBean<>(false, List.of());
        }
        List<SocialNoticeDo> noticeDoList = socialNoticeDao.selectList(new LambdaQueryWrapper<SocialNoticeDo>()
                .in(SocialNoticeDo::getNoticeType, SysConstant.NOTICE_SYSTEM,
                        SysConstant.NOTICE_AUDIT_ARTICLE,
                        SysConstant.NOTICE_AUDIT_EASSAY,
                        SysConstant.NOTICE_AUDIT_COLUMN,
                        SysConstant.NOTICE_AUDIT_DOMAIN)
                .eq(SocialNoticeDo::getUserId, userId)
                .eq(data.getReadStatus() != null, SocialNoticeDo::getReadStatus, data.getReadStatus())
                .orderByDesc(SocialNoticeDo::getCreateTime)
                .last(" limit " + data.getOffset() + "," + data.getLimit()));
        if (CollectionUtils.isEmpty(noticeDoList)) {
            return new PageBean<>(false, List.of());
        }
        List<Long> articleIds = new ArrayList<>();
        List<Long> columnIds = new ArrayList<>();
        List<Long> essayIds = new ArrayList<>();
        noticeDoList.stream()
                .peek(record -> {
                    int noticeType = record.getNoticeType();
                    if (noticeType == SysConstant.NOTICE_AUDIT_ARTICLE || noticeType == SysConstant.NOTICE_SYSTEM) {
                        articleIds.add(record.getTargetId());
                    } else if (noticeType == SysConstant.NOTICE_AUDIT_COLUMN) {
                        columnIds.add(record.getTargetId());
                    } else if (noticeType == SysConstant.NOTICE_AUDIT_EASSAY) {
                        essayIds.add(record.getTargetId());
                    }
                })
                .collect(Collectors.toList());
        Map<Long, PublicArticleVo> articleMap = new HashMap<>();
        Map<Long, PublicArticleVo> columnMap = new HashMap<>();
        Map<Long, RamblyJotVo> essayMap = new HashMap<>();
        // 系统通知分为两类，系统活动和升级为一类，另一类为文章随笔审核失败说明
        BaseRequest<Collection<Long>> baseRequest = new BaseRequest<>(articleIds);
        if (articleIds.size() > 0) {
            ResponseBean<List<PublicArticleVo>> articleList = cpsService.getArticleListByIds(baseRequest, userId);
            if (articleList.isResult() && !CollectionUtils.isEmpty(articleList.getData())) {
                articleMap.putAll(articleList.getData().stream()
                        .collect(Collectors.toMap(PublicArticleVo::getUid, Function.identity(), (a, b) -> b)));
            }
        }
        if (essayIds.size() > 0) {
            baseRequest = new BaseRequest<>(essayIds);
            ResponseBean<List<RamblyJotVo>> essayList = cpsService.getRamblyjotListByIds(baseRequest, userId);
            if (essayList.isResult() && !CollectionUtils.isEmpty(essayList.getData())) {
                essayMap.putAll(essayList.getData().stream()
                        .collect(Collectors.toMap(RamblyJotVo::getUid, Function.identity(), (a, b) -> b)));
            }
        }
        List<Long> systemNoticeIds = new ArrayList<>();
        List<SystemNoticeVo> collect = noticeDoList.stream()
                .map(record -> {
                    SystemNoticeVo noticeVo = new SystemNoticeVo();
                    BeanUtil.copyProperties(record, noticeVo);
                    int noticeType = record.getNoticeType();
                    noticeVo.setTargetType(noticeType == SysConstant.NOTICE_AUDIT_EASSAY ? 3 : 1);
                    // 系统通知、文章审核失败
                    if (noticeType == SysConstant.NOTICE_SYSTEM || noticeType == SysConstant.NOTICE_AUDIT_ARTICLE) {
                        PublicArticleVo articleVo = articleMap.get(record.getTargetId());
                        if (articleVo == null) {
                            return null;
                        }
                        systemNoticeIds.add(record.getUid());
                        if (noticeType == SysConstant.NOTICE_SYSTEM) {
                            noticeVo.setContent(articleVo.getTitle());
                            noticeVo.setLabel(articleVo.getSummary());
                        } else {
                            String content = "文章 " + articleVo.getTitle() + " 发布失败，" + record.getNoticeContent();
                            noticeVo.setContent(content);
                            noticeVo.setLabel("前往查看");
                        }
                        noticeVo.setArticleInfo(articleVo);
                    } else {
                        // 随笔审核失败
                        RamblyJotVo ramblyJotVo = essayMap.get(record.getTargetId());
                        if (ramblyJotVo == null) {
                            return null;
                        }
                        systemNoticeIds.add(record.getUid());
                        String content = "随笔 " + ramblyJotVo.getTitle() + " 发布失败，" + record.getNoticeContent();
                        noticeVo.setContent(content);
                        noticeVo.setLabel("前往查看");
                        noticeVo.setRamblyJot(ramblyJotVo);
                    }
                    return noticeVo;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (systemNoticeIds.size() > 0) {
            socialNoticeDao.update(new UpdateWrapper<SocialNoticeDo>()
                    .in(SysConstant.ES_FILED_UID, systemNoticeIds)
                    .set("read_status", 1));
        }
        return new PageBean<>(true, collect);
    }
}
