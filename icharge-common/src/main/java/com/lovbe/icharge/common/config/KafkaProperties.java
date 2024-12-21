package com.lovbe.icharge.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 10:37
 * @Description: kafka属性
 */
@Data
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {
    /**
     * kafka集群/单机地址
     */
    private String servers;
    /**
     * 开启事务的id前缀
     */
    private String transactionIdPrefix;
    /**
     * 鉴权用户名
     */
    private String saslUsername;
    /**
     * 鉴权密码
     */
    private String saslPassword;
    /**
     * ack方式 0，1，all
     */
    private String acks;
    /**
     * 失败重试次数
     */
    private String retries;
    /**
     * 批量拉去
     */
    private String batchSize;
    /**
     * 批量拉取数量
     */
    private int batchPoll = 100;
    /**
     * 缓冲区大小
     */
    private String bufferMemory;
    /**
     * 消费偏移模式: latest（从最新的记录开始读取,消费者只关心新消息，不关心历史消息）
     *             earliest (从最早的消息开始读取，从每个分区的最早记录开始读取，适用于需要处理所有历史消息的场景)
     */
    private String kafkaAutoOffset = "latest";
    /**
     * app名称
     */
    @Value("${spring.application.name}")
    private String appName;
}
