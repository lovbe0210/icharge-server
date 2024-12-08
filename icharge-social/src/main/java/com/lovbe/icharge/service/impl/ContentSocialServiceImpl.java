package com.lovbe.icharge.service.impl;

import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.model.base.KafkaMessage;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import com.lovbe.icharge.entity.dto.LikeActionDo;
import com.lovbe.icharge.service.ContentSocialService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static cn.hutool.http.ContentType.JSON;

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
    // 用户操作：点赞
    @Value("${spring.kafka.topics.user-action-like}")
    private String likeActionTopic;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public void marksContent(ContentLikeDTO data, Long userId) {
        LikeActionDo actionDo = new LikeActionDo(data.getTargetId(), data.getTargetType(), userId);
        actionDo.setCreateTime(new Date());
        KafkaMessage message = new KafkaMessage<>(appName, likeActionTopic, actionDo);
        try {
            CompletableFuture send = kafkaTemplate.send(likeActionTopic, JSONUtil.toJsonStr(message));
            send.thenAccept(result -> {
                log.info(JSONUtil.toJsonStr(result));
                log.info("[send-message]--消息发送成功， sid：{}", message.getMsgId());
            }).exceptionally(ex -> {
                log.error("[send-message]--消息发送失败，cause: {}, sendData: {}", ex.toString(), JSONUtil.toJsonStr(message));
                return null;
            });
        } catch (Exception e) {
            log.error("[send-message]--消息发送失败，kafka服务不可用, sendData: {}", JSONUtil.toJsonStr(message));
        }
    }
}
