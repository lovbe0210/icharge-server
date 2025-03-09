package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
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
import com.lovbe.icharge.entity.vo.LikeNoticeVo;
import com.lovbe.icharge.entity.vo.SocialNoticeVo;
import com.lovbe.icharge.entity.vo.SystemNoticeVo;
import com.lovbe.icharge.service.MessageNoticeService;
import com.lovbe.icharge.service.feign.ContentPickService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
                        if (Objects.equals(noticeDo.getNoticeType(), SysConstant.TARGET_TYPE_ARTICLE)) {
                            likeNotice.setArticleInfo(articleVoMap.get(noticeDo.getTargetId()));
                        }
                        if (Objects.equals(noticeDo.getNoticeType(), SysConstant.TARGET_TYPE_ESSAY)) {
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
        List<SocialNoticeDo> noticeDoList = socialNoticeDao.selectList(data, userId, SysConstant.NOTICE_FOLLOW);
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
                .eq(SocialNoticeDo::getNoticeType, SysConstant.NOTICE_SYSTEM)
                .eq(SocialNoticeDo::getUserId, userId)
                .eq(data.getReadStatus() != null, SocialNoticeDo::getReadStatus, data.getReadStatus()));
        if (count == null || count == 0) {
            return new PageBean<>(0, List.of());
        }
        List<SocialNoticeDo> noticeDoList = socialNoticeDao.selectList(data, userId, SysConstant.NOTICE_SYSTEM);
        List<Long> articleIds = new ArrayList<>();
        List<SystemNoticeVo> collect = noticeDoList.stream()
                .map(record -> {
                    SystemNoticeVo noticeVo = new SystemNoticeVo();
                    BeanUtil.copyProperties(record, noticeVo);
                    articleIds.add(record.getTargetId());
                    return noticeVo;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return new PageBean<>(0, List.of());
        }
        // 系统通知暂时知识文章或专栏文章形式出现
        BaseRequest<Collection<Long>> baseRequest = new BaseRequest<>(articleIds);
        ResponseBean<List<PublicArticleVo>> articleList = cpsService.getArticleListByIds(baseRequest, userId);
        if (articleList.isResult() && !CollectionUtils.isEmpty(articleList.getData())) {
            Map<Long, PublicArticleVo> articleMap = articleList.getData().stream()
                    .collect(Collectors.toMap(PublicArticleVo::getUid, Function.identity(), (a, b) -> b));
            List<Long> systemNoticeIds = new ArrayList<>();
            collect = collect.stream()
                    .map(noticeVo -> {
                        PublicArticleVo articleVo = articleMap.get(noticeVo.getTargetId());
                        if (articleVo == null) {
                            return null;
                        }
                        systemNoticeIds.add(noticeVo.getUid());
                        noticeVo.setContent(articleVo.getTitle());
                        noticeVo.setLabel(articleVo.getSummary());
                        String url = "/" + articleVo.getDomain() + "/" +
                                (StringUtils.hasLength(articleVo.getColumnUri()) ? ("/" + articleVo.getColumnUri() + "/") : "") +
                                articleVo.getUri();
                        noticeVo.setUrl(url);
                        return noticeVo;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            socialNoticeDao.update(new UpdateWrapper<SocialNoticeDo>()
                    .in(SysConstant.ES_FILED_UID, systemNoticeIds)
                    .set("read_status", 1));
            return new PageBean<>(Math.toIntExact(count), collect);
        } else {
            return new PageBean<>(0, List.of());
        }
    }
}
