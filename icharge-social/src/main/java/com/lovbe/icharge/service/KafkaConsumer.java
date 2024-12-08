package com.lovbe.icharge.service;

import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.model.base.KafkaMessage;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.entity.dto.LikeActionDo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

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
    private SocialLikeService socialLikeService;
    /**
     * 点赞列表消息消费
     * @param consumerRecord
     * @param ack
     * @param consumer
     */
    @KafkaListener(topics = "${spring.kafka.topics.user-action-like}",
            containerFactory = "kafkaListenerContainerFactory", groupId = "action-like")
    public void listenActionLike(ConsumerRecord consumerRecord, Acknowledgment ack, Consumer consumer) {
        try {
            String data = String.valueOf(consumerRecord.value());
            KafkaMessage kafkaMsg = JsonUtils.parseObject(data, KafkaMessage.class);
            if (log.isDebugEnabled()) {
                log.debug("received msg: " + data);
            }
            Object msgData = kafkaMsg.getData();
            if(msgData == null) {
                log.error("消息丢弃: {}, 原因: 消息体内容为空", data);
                return;
            }
            LikeActionDo actionDo = JsonUtils.parseObject(JSONUtil.toJsonStr(msgData), LikeActionDo.class);
            // 参数校验
            if (actionDo.getUserId() == null ||
                    actionDo.getTargetId() == null ||
                    actionDo.getTargetType() == null ||
                    actionDo.getCreateTime() == null) {
                log.error("消息丢弃: {}, 原因: 消息体缺少非空参数", data);
                return;
            }
            socialLikeService.handlerLikeAction(actionDo);
        } catch (Exception e) {
            log.error("消息消费失败: {}, 原因: {}", consumer, e.toString());
        } finally {
            ack.acknowledge();
        }
    }
}
