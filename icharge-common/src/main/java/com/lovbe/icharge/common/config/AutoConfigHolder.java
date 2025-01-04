package com.lovbe.icharge.common.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 16:31
 * @Description: MS
 */
@Configuration
@EnableConfigurationProperties({KafkaProperties.class, AIPromptProperties.class, ElasticsearchProperties.class})
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@Import({
        KafkaProduceConfig.class,
        KafkaConsumerConfig.class,
        RedisConfig.class,
        ElasticsearchConfig.class
})
public class AutoConfigHolder {
}
