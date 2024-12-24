package com.lovbe.icharge.service;

import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.model.base.KafkaMessage;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.entity.dto.LikeActionDo;
import com.lovbe.icharge.entity.dto.ReplyCommentDo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 16:57
 * @Description: kafka消息处理
 */
@Slf4j
@Component
@RefreshScope
public class KafkaConsumer {
    @Resource
    private SocialActionService socialActionService;

    /**
     * 点赞消息消费
     *
     * @param consumerRecords
     * @param ack
     */
    @KafkaListener(topics = "${spring.kafka.topics.user-action-like}",
            containerFactory = "kafkaListenerContainerFactory", groupId = "action-like")
    public void listenActionLike(List<ConsumerRecord> consumerRecords, Acknowledgment ack) {
        if (consumerRecords.isEmpty()) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("received msgSize: " + consumerRecords.size());
        }
        try {
            List<LikeActionDo> collect = consumerRecords.parallelStream()
                    .map(consumerRecord -> {
                        String data = String.valueOf(consumerRecord.value());
                        KafkaMessage kafkaMsg = JsonUtils.parseObject(data, KafkaMessage.class);
                        if (log.isDebugEnabled()) {
                            log.debug("received msg: " + data);
                        }
                        Object msgData = kafkaMsg.getData();
                        if (msgData == null) {
                            log.error("消息丢弃: {}, 原因: 消息体内容为空", data);
                            return null;
                        }
                        LikeActionDo actionDo = JsonUtils.parseObject(JSONUtil.toJsonStr(msgData), LikeActionDo.class);
                        // 参数校验
                        if (actionDo.getUserId() == null ||
                                actionDo.getAction() == null ||
                                actionDo.getTargetId() == null ||
                                actionDo.getTargetType() == null ||
                                actionDo.getCreateTime() == null) {
                            log.error("消息丢弃: {}, 原因: 消息体缺少非空参数", data);
                            return null;
                        }
                        return actionDo;
                    })
                    .filter(actionDo -> actionDo != null).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                return;
            }
            socialActionService.handlerLikeAction(collect);
        } catch (Exception e) {
            log.error("[点赞消息消费] --- 消息消费失败, errorInfo: {}", e.toString());
        } finally {
            ack.acknowledge();
        }
    }

    /**
     * 评论回复消息消费
     *
     * @param consumerRecords
     * @param ack
     */
    @KafkaListener(topics = "${spring.kafka.topics.user-action-comment}",
            containerFactory = "kafkaListenerContainerFactory", groupId = "action-comment")
    public void listenActionComment(List<ConsumerRecord> consumerRecords, Acknowledgment ack) {
        if (consumerRecords.isEmpty()) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("received msgSize: " + consumerRecords.size());
        }
        try {
            List<ReplyCommentDo> collect = consumerRecords.parallelStream()
                    .map(consumerRecord -> {
                        String data = String.valueOf(consumerRecord.value());
                        KafkaMessage kafkaMsg = JsonUtils.parseObject(data, KafkaMessage.class);
                        if (log.isDebugEnabled()) {
                            log.debug("received msg: " + data);
                        }
                        Object msgData = kafkaMsg.getData();
                        if (msgData == null) {
                            log.error("消息丢弃: {}, 原因: 消息体内容为空", data);
                            return null;
                        }
                        ReplyCommentDo actionDo = JsonUtils.parseObject(JSONUtil.toJsonStr(msgData), ReplyCommentDo.class);
                        // 参数校验
                        if (actionDo.getUserId() == null ||
                                actionDo.getTargetId() == null ||
                                actionDo.getTargetType() == null ||
                                actionDo.getContent() == null ||
                                (actionDo.getParentId() == null && actionDo.getReplyUserId() != null) ||
                                actionDo.getUpdateTime() == null ||
                                actionDo.getCreateTime() == null) {
                            log.error("消息丢弃: {}, 原因: 消息体缺少非空参数", data);
                            return null;
                        }
                        return actionDo;
                    })
                    .filter(actionDo -> actionDo != null).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                return;
            }
            socialActionService.handlerCommentAction(collect);
        } catch (Exception e) {
            log.error("[点赞消息消费] --- 消息消费失败, errorInfo: {}", e.toString());
        } finally {
            ack.acknowledge();
        }
    }
}
