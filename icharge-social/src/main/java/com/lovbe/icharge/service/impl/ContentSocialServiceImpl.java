package com.lovbe.icharge.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.KafkaMessage;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.dao.ReplyCommentDao;
import com.lovbe.icharge.dao.SocialLikeDao;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import com.lovbe.icharge.entity.dto.LikeActionDo;
import com.lovbe.icharge.entity.dto.ReplyCommentDo;
import com.lovbe.icharge.entity.dto.TargetCommentDTO;
import com.lovbe.icharge.service.ContentSocialService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/5 0:37
 * @Description: MS
 */
@Slf4j
@Service
public class ContentSocialServiceImpl implements ContentSocialService {
    @Resource
    private KafkaTemplate kafkaTemplate;
    @Resource
    private ReplyCommentDao replyCommentDao;
    // 用户操作：点赞
    @Value("${spring.kafka.topics.user-action-like}")
    private String likeActionTopic;
    @Value("${spring.application.name}")
    private String appName;

    @Resource
    private SocialLikeDao socialLikeDao;

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
        KafkaMessage message = new KafkaMessage<>(appName, likeActionTopic, actionDo);
        try {
            CompletableFuture send = kafkaTemplate.send(likeActionTopic, JSONUtil.toJsonStr(message));
            send.thenAccept(result -> {
                log.info("[send-message]--消息发送成功， sid：{}", message.getMsgId());
            }).exceptionally(ex -> {
                log.error("[send-message]--消息发送失败，cause: {}, sendData: {}", ex.toString(), JSONUtil.toJsonStr(message));
                return null;
            });
        } catch (Exception e) {
            log.error("[send-message]--消息发送失败，kafka服务不可用, sendData: {}", JSONUtil.toJsonStr(message));
        }
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
        return ResponseBean.ok(commentResult);
    }
}
