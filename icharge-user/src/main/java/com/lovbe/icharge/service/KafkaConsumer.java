package com.lovbe.icharge.service;

import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.model.base.KafkaMessage;
import com.lovbe.icharge.common.model.dto.ContentPublishDTO;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.entity.dto.CodeLogDo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 16:57
 * @Description: kafka消息处理
 */
@Slf4j
@Component
public class KafkaConsumer {
    @Resource
    private UserService userService;
    @Resource
    private SimpleCodeService simpleCodeService;


    /**
     * 个人主页内容更新消息消费
     *
     * @param consumerRecords
     * @param ack
     */
    @KafkaListener(topics = "${spring.kafka.topics.action-domain-content}",
            containerFactory = "kafkaListenerContainerFactory", groupId = "domain-content")
    public void listenColumnContent(List<ConsumerRecord> consumerRecords, Acknowledgment ack) {
        if (consumerRecords.isEmpty()) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("received msgSize: " + consumerRecords.size());
        }
        try {
            List<ContentPublishDTO> collect = getContentPublishDTOS(consumerRecords);
            if (collect == null) return;
            userService.handlerPublishAction(collect);
        } catch (Exception e) {
            log.error("[个人主页更新消息消费] --- 消息消费失败, errorInfo: {}", e.toString());
        } finally {
            ack.acknowledge();
        }
    }

    /**
     * 发送短信/邮箱验证码
     *
     * @param consumerRecords
     * @param ack
     */
    @KafkaListener(topics = "${spring.kafka.topics.send-code}",
            containerFactory = "kafkaListenerContainerFactory", groupId = "verify-code")
    public void listenSendVerifyCode(List<ConsumerRecord> consumerRecords, Acknowledgment ack) {
        if (consumerRecords.isEmpty()) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("received msgSize: " + consumerRecords.size());
        }
        try {
            List<CodeLogDo> collect = getVerifyCodeDTOS(consumerRecords);
            if (collect == null) return;
            simpleCodeService.handlerSendVerifyCode(collect);
        } catch (Exception e) {
            log.error("[发送验证码消息消费] --- 消息消费失败, errorInfo: {}", e.toString());
        } finally {
            ack.acknowledge();
        }
    }

    private List<CodeLogDo> getVerifyCodeDTOS(List<ConsumerRecord> consumerRecords) {
        List<CodeLogDo> collect = consumerRecords.parallelStream()
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
                    CodeLogDo codeLogDO = JsonUtils.parseObject(JSONUtil.toJsonStr(msgData), CodeLogDo.class);
                    // 参数校验
                    if (codeLogDO.getSceneCode() == null ||
                            codeLogDO.getTitle() == null ||
                            codeLogDO.getContent() == null ||
                            (codeLogDO.getMobile() == null &&
                            codeLogDO.getEmail() == null)) {
                        log.error("消息丢弃: {}, 原因: 消息体缺少非空参数", data);
                        return null;
                    }
                    return codeLogDO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return null;
        }
        return collect;
    }

    /**
     * @description: 获取发布消息体
     * @param: consumerRecords
     * @return: java.util.List<com.lovbe.icharge.common.model.dto.ContentPublishDTO>
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
