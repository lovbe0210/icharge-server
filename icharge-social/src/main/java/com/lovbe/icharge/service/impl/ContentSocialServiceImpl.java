package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.dao.CommonDao;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.dao.ReplyCommentDao;
import com.lovbe.icharge.dao.SocialLikeDao;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.ReplyCommentVo;
import com.lovbe.icharge.service.ContentSocialService;
import com.lovbe.icharge.service.feign.StorageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/5 0:37
 * @Description: MS
 */
@Slf4j
@Service
public class ContentSocialServiceImpl implements ContentSocialService {
    @Resource
    private ReplyCommentDao replyCommentDao;
    @Resource
    private StorageService storageService;
    // 用户操作：点赞
    @Value("${spring.kafka.topics.user-action-like}")
    private String likeActionTopic;
    // 用户操作：评论回复
    @Value("${spring.kafka.topics.user-action-comment}")
    private String replyCommentTopic;
    @Value("${spring.application.name}")
    private String appName;

    @Resource
    private SocialLikeDao socialLikeDao;
    @Resource
    private CommonService commonService;

    @Override
    public void contentLikeMark(ContentLikeDTO data, Long userId) {
        LikeActionDo actionDo = new LikeActionDo(data.getTargetId(), data.getTargetType(), userId, data.getAction());
        actionDo.setCreateTime(new Date());
        // redis同步操作
        String userLikesSetKey = RedisKeyConstant.getUserLikesSet(userId);
        String targetLikedSetKey = RedisKeyConstant.getTargetLikedSet(data.getTargetId());
        if (data.getAction() == 1) {
            // 点赞操作
            RedisUtil.zset(userLikesSetKey, actionDo.getCreateTime().getTime(), data.getTargetId());
            RedisUtil.zset(targetLikedSetKey, actionDo.getCreateTime().getTime(), userId);
        } else {
            // 取消点赞
            RedisUtil.zRemove(userLikesSetKey, data.getTargetId());
            RedisUtil.zRemove(targetLikedSetKey, userId);
        }
        // 消息队列异步纠正
        commonService.sendMessage(appName, likeActionTopic, actionDo);
    }

    @Override
    public boolean ifLike(ContentLikeDTO data, Long userId) {
        LikeActionDo actionDo = socialLikeDao.selectOne(new LambdaQueryWrapper<LikeActionDo>()
                        .eq(LikeActionDo::getTargetId, data.getTargetId())
                        .eq(LikeActionDo::getUserId, userId),
                false);
        return actionDo != null;
    }

    @Override
    public ResponseBean getCommentList(BaseRequest<TargetCommentDTO> baseRequest, Long userId) {
        HashMap<String, Object> commentResult = new HashMap<>(2);
        // 查询评论总数
        TargetCommentDTO commentDTO = baseRequest.getData();
        TargetStatisticDo statisticDo = replyCommentDao.selectCommentStatistic(commentDTO);
        if (statisticDo == null || statisticDo.getCommentCount() == 0) {
            commentResult.put(SysConstant.TOTAL, 0);
            commentResult.put(SysConstant.LIST, List.of());
            return ResponseBean.ok(commentResult);
        }
        commentResult.put(SysConstant.TOTAL, statisticDo.getCommentCount());
        // 获取评论列表
        List<ReplyCommentDo> replyCommentList = replyCommentDao.selectReplyCommentList(commentDTO);
        if (CollectionUtils.isEmpty(replyCommentList)) {
            commentResult.put(SysConstant.LIST, List.of());
            return ResponseBean.ok(commentResult);
        }
        // 如果为登录用户，则获取每条评论的点赞状态
        Set<Object> likeTargets = new HashSet<>();
        if (userId != null) {
            String userLikedSet = RedisKeyConstant.getUserLikesSet(userId);
            Set<Object> rangeSet = RedisUtil.zsGetSet(userLikedSet, 0, -1);
            if (!CollectionUtils.isEmpty(rangeSet)) {
                likeTargets.addAll(rangeSet);
            }
        }
        List<ReplyCommentVo> collect = replyCommentList.stream()
                .map(replyCommentDo -> {
                    ReplyCommentVo replyCommentVo = new ReplyCommentVo();
                    BeanUtil.copyProperties(replyCommentDo, replyCommentVo);
                    if (replyCommentVo.getUserInfo() != null) {
                        replyCommentVo.setUserInfo(commonService.getCacheUser(replyCommentVo.getUserInfo().getUid()));
                    }
                    replyCommentVo.setIfLike(likeTargets.contains(replyCommentDo.getUid()) ? 1 : 0);
                    if (CollectionUtils.isEmpty(replyCommentDo.getReplyCommentList())) {
                        replyCommentVo.setReplyList(List.of());
                        return replyCommentVo;
                    }
                    List<ReplyCommentVo> deepReplyList = replyCommentDo.getReplyCommentList().stream()
                            .map(deepReply -> {
                                ReplyCommentVo deepReplyVo = new ReplyCommentVo();
                                BeanUtil.copyProperties(deepReply, deepReplyVo);
                                if (deepReplyVo.getUserInfo() != null) {
                                    deepReplyVo.setUserInfo(commonService.getCacheUser(deepReplyVo.getUserInfo().getUid()));
                                }
                                if (deepReplyVo.getReplyUserInfo() != null) {
                                    deepReplyVo.setReplyUserInfo(commonService.getCacheUser(deepReplyVo.getReplyUserInfo().getUid()));
                                }
                                replyCommentVo.setIfLike(likeTargets.contains(deepReply.getUid()) ? 1 : 0);
                                return deepReplyVo;
                            }).collect(Collectors.toList());
                    replyCommentVo.setReplyList(deepReplyList);
                    return replyCommentVo;
                }).collect(Collectors.toList());
        commentResult.put(SysConstant.LIST, collect);
        return ResponseBean.ok(commentResult);
    }

    @Override
    public ReplyCommentVo replyComment(ReplyCommentDTO replyCommentDTO, Long userId) {
        // 判断当前内容是否开启了评论功能
        Integer enableComment = replyCommentDao.selectEnableComment(replyCommentDTO);
        if (enableComment == null && replyCommentDTO.getTargetType() == SysConstant.TARGET_TYPE_ARTICLE) {
            throw new ServiceException(ServiceErrorCodes.COMMENT_NOT_ENABLE_FAILED);
        } else if (enableComment != null && enableComment == 0) {
            throw new ServiceException(ServiceErrorCodes.COMMENT_NOT_ENABLE_FAILED);
        }

        // 业务参数校验
        Long replyUserId = replyCommentDTO.getReplyUserId();
        if (replyUserId != null) {
            Assert.notNull(replyCommentDTO.getParentId(), ServiceErrorCodes.REPLY_PARENT_ID_NOT_NULL.getMsg());
        }
        ReplyCommentDo replyCommentDo = new ReplyCommentDo();
        BeanUtils.copyProperties(replyCommentDTO, replyCommentDo);
        // 图片文件上传
        MultipartFile contentImgFile = replyCommentDTO.getContentImgFile();
        if (contentImgFile != null) {
            // 上传文件
            ResponseBean<String> upload = storageService
                    .upload(new FileUploadDTO(contentImgFile, SysConstant.FILE_SCENE_COMMENT));
            if (!upload.isResult()) {
                log.error("[发表评论回复] --- 图片上传失败，errorInfo: {}", upload.getMessage());
                throw new ServiceException(ServiceErrorCodes.COMMENT_IMAGE_UPLOAD_FAILED);
            }
            replyCommentDo.setContentImgUrl(upload.getData());
        }
        replyCommentDo.setUserId(userId)
                .setUid(YitIdHelper.nextId())
                .setStatus(CommonStatusEnum.NORMAL.getStatus())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        ReplyCommentVo replyCommentVo = new ReplyCommentVo();
        BeanUtil.copyProperties(replyCommentDo, replyCommentVo);
        // 补充userInfo
        replyCommentVo.setUserInfo(commonService.getCacheUser(userId));
        if (replyCommentDTO.getReplyUserId() != null) {
            replyCommentVo.setReplyUserInfo(commonService.getCacheUser(replyCommentDTO.getReplyUserId()));
        }
        // 消息队列异步统计
        commonService.sendMessage(appName, replyCommentTopic, replyCommentDo);
        return replyCommentVo;
    }

    @Override
    public List<ReplyCommentVo> getCommentReplyList(BaseRequest<TargetCommentDTO> baseRequest, Long userId) {
        List<ReplyCommentDo> replyCommentList = replyCommentDao.selectCommentReplyList(baseRequest.getData());
        if (CollectionUtils.isEmpty(replyCommentList)) {
            return List.of();
        }
        // 如果为登录用户，则获取每条评论的点赞状态
        Set<Object> likeTargets = new HashSet<>();
        if (userId != null) {
            String userLikedSet = RedisKeyConstant.getUserLikesSet(userId);
            Set<Object> rangeSet = RedisUtil.zsGetSet(userLikedSet, 0, -1);
            if (!CollectionUtils.isEmpty(rangeSet)) {
                likeTargets.addAll(rangeSet);
            }
        }
        List<ReplyCommentVo> collect = replyCommentList.stream()
                .map(replyCommentDo -> {
                    ReplyCommentVo replyVo = new ReplyCommentVo();
                    BeanUtil.copyProperties(replyCommentDo, replyVo);
                    if (replyCommentDo.getUserInfo() != null) {
                        replyVo.setUserInfo(commonService.getCacheUser(replyCommentDo.getUserInfo().getUid()));
                    }
                    if (replyCommentDo.getReplyUserInfo() != null) {
                        replyVo.setReplyUserInfo(commonService.getCacheUser(replyCommentDo.getReplyUserInfo().getUid()));
                    }
                    if (userId != null) {
                        replyVo.setIfLike(likeTargets.contains(replyCommentDo.getUid()) ? 1 : 0);
                    }
                    return replyVo;
                }).collect(Collectors.toList());
        return collect;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteReplyComment(Long uid, Long userId) {
        ReplyCommentDo replyCommentDo = replyCommentDao.selectById(uid);
        if (replyCommentDo == null) {
            return;
        }

        if (!Objects.equals(replyCommentDo.getUserId(), userId)) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        int deleteCount = 1;
        if (replyCommentDo.getParentId() == null) {
            // 删除楼中楼回复
            deleteCount += replyCommentDao.delete(new LambdaQueryWrapper<ReplyCommentDo>()
                    .eq(ReplyCommentDo::getParentId, uid));
        } else {
            // 更新父级评论的统计数
            replyCommentDao.updateReplyCountBySub(replyCommentDo.getParentId(), deleteCount);
        }
        replyCommentDao.deleteById(uid);
        // 更新target统计表
        replyCommentDao.updateReplyCountBySub(replyCommentDo.getTargetId(), deleteCount);
    }
}
