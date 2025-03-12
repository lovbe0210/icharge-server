package com.lovbe.icharge.service;

import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.config.KafkaProperties;
import com.lovbe.icharge.common.factory.KafkaConsumeFactory;
import com.lovbe.icharge.common.model.base.KafkaMessage;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.entity.dto.ContentPublishDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
@EnableScheduling
public class KafkaConsumer {
    @Resource
    private ArticleService articleService;
    @Resource
    private RamblyJotService ramblyJotService;
    @Resource
    private KafkaProperties kafkaProperties;

    /**
     * @description: 文章发布消息处理 系统启动后每5分钟消费一次
     * @author: lovbe0210
     * @date: 2025/2/11 21:25
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 10 * 1000)
    public void scheduledListenArticlePublish() {
        List<ConsumerRecord> consumerRecords = KafkaConsumeFactory.pollMessage("article-publish",
                kafkaProperties.getArticlePublishTopic(), kafkaProperties.getBatchPoll());
        if (CollectionUtils.isEmpty(consumerRecords)) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("received msgSize: " + consumerRecords.size());
        }
        try {
            List<ContentPublishDTO> collect = getContentPublishDTOS(consumerRecords);
            if (collect == null) return;
            articleService.handlerPublishAction(collect);
        } catch (Exception e) {
            log.error("[文章发布消息消费] --- 消息消费失败, errorInfo: {}", e.toString());
        }
    }

    /**
     * 文章内容发布消息消费
     *
     * @param consumerRecords
     * @param ack
     */
//    @KafkaListener(topics = "${spring.kafka.topics.action-content-publish}",
//            containerFactory = "kafkaListenerContainerFactory", groupId = "article-publish")
    public void listenArticlePublish(List<ConsumerRecord> consumerRecords, Acknowledgment ack) {
        if (consumerRecords.isEmpty()) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("received msgSize: " + consumerRecords.size());
        }
        try {
            List<ContentPublishDTO> collect = getContentPublishDTOS(consumerRecords);
            if (collect == null) return;
            articleService.handlerPublishAction(collect);
        } catch (Exception e) {
            log.error("[文章发布消息消费] --- 消息消费失败, errorInfo: {}", e.toString());
        } finally {
            ack.acknowledge();
        }
    }

    /**
     * 随笔内容发布消息消费
     *
     * @param consumerRecords
     * @param ack
     */
    @KafkaListener(topics = "${spring.kafka.topics.action-essay-publish}",
            containerFactory = "kafkaListenerContainerFactory", groupId = "essay-publish")
    public void listenEssayPublish(List<ConsumerRecord> consumerRecords, Acknowledgment ack) {
        if (consumerRecords.isEmpty()) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("received msgSize: " + consumerRecords.size());
        }
        try {
            List<ContentPublishDTO> collect = getContentPublishDTOS(consumerRecords);
            if (collect == null) return;
            ramblyJotService.handlerPublishAction(collect);
        } catch (Exception e) {
            log.error("[随笔发布消息消费] --- 消息消费失败, errorInfo: {}", e.toString());
        } finally {
            ack.acknowledge();
        }
    }

    /**
     * @description: 获取发布消息体
     * @param: consumerRecords
     * @return: java.util.List<com.lovbe.icharge.entity.dto.ContentPublishDTO>
     * @author: lovbe0210
     * @date: 2025/1/27 2:30
     */
    private static List<ContentPublishDTO> getContentPublishDTOS(List<ConsumerRecord> consumerRecords) {
        List<ContentPublishDTO> collect = consumerRecords.parallelStream()
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
                    ContentPublishDTO publishDTO = JsonUtils.parseObject(JSONUtil.toJsonStr(msgData), ContentPublishDTO.class);
                    // 参数校验
                    if (publishDTO.getTargetId() == null ||
                            publishDTO.getContentId() == null ||
                            publishDTO.getTargetType() == null ||
                            publishDTO.getPublishTime() == null) {
                        log.error("消息丢弃: {}, 原因: 消息体缺少非空参数", data);
                        return null;
                    }
                    return publishDTO;
                })
                .filter(actionDo -> actionDo != null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return null;
        }
        return collect;
    }
}
