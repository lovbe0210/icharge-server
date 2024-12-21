package com.lovbe.icharge.common.config;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.*;
import org.springframework.util.StringUtils;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 11:23
 * @Description: 消费者配置
 */
@Slf4j
@EnableKafka
@ConditionalOnProperty(value = "spring.kafka.consume-enable", havingValue = "true")
public class KafkaConsumerConfig {
    /**
     * 定义一个KafkaAdmin的bean，可以自动检测集群中是否存在topic，不存在则创建
     */
    @Bean
    public KafkaAdmin kafkaAdmin(KafkaProperties kafkaProperties) {
        Map<String, Object> configs = new HashMap<>();
        // 指定多个kafka集群多个地址，例如：192.168.2.11,9092,192.168.2.12:9092,192.168.2.13:9092
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getServers());
        return new KafkaAdmin(configs);
    }

    /**
     * 构造消费者属性map，ConsumerConfig中的可配置属性比spring boot自动配置要多
     */
    private Map<String, Object> consumerProperties(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(16);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getBatchPoll());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getServers());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getKafkaAutoOffset());
        if (!StringUtils.isEmpty(kafkaProperties.getSaslUsername()) && !StringUtils.isEmpty(kafkaProperties.getSaslPassword())) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            String jsajConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\""
                    + kafkaProperties.getSaslUsername()
                    + "\" password=\"" +
                    kafkaProperties.getSaslPassword()
                    + "\";";
            props.put("sasl.jaas.config", jsajConfig);
        }
        return props;
    }

    /**
     * 不使用spring boot默认方式创建的DefaultKafkaConsumerFactory，重新定义创建方式
     *
     * @return
     */
    @Bean("consumerFactory")
    public DefaultKafkaConsumerFactory consumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory(consumerProperties(kafkaProperties));
    }

    public CommonErrorHandler commonErrorHandler() {
        // 创建 FixedBackOff 对象
        BackOff backOff = new FixedBackOff(5000L, 3L);
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler((ConsumerAwareRecordRecoverer) (record, consumer, exception) -> {
            log.info("[消息消费失败] --- errorInfo: {}, data: {}" + exception.toString(), JSONUtil.toJsonStr(record.value()));
            // TODO 放入死信队列
        }, backOff);
        return defaultErrorHandler;
    }

    @Bean("kafkaListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory(KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaProperties));
        // 开启批量消费
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(commonErrorHandler());

        ContainerProperties containerProperties = factory.getContainerProperties();
        // 是否设置手动提交
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}
