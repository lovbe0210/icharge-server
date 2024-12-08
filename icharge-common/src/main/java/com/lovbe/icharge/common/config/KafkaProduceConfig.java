package com.lovbe.icharge.common.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 10:50
 * @Description: kafka生产者配置类
 */
@ConditionalOnProperty(value = "spring.kafka.produce-enable", havingValue = "true")
public class KafkaProduceConfig {
    @Bean
    public Map<String, Object> producerConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getServers());
        //acks=0 ： 生产者在成功写入消息之前不会等待任何来自服务器的响应。
        //acks=1 ： 只要集群的首领节点收到消息，生产者就会收到一个来自服务器成功响应。
        //acks=all ：只有当所有参与复制的节点全部收到消息时，生产者才会收到一个来自服务器的成功响应。
        //开启事务必须设为all
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getAcks());
        //发生错误后，消息重发的次数，开启事务必须大于0
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getRetries());
        //当多个消息发送到相同分区时,生产者会将消息打包到一起,以减少请求交互. 而不是一条条发送
        //批次的大小可以通过batch.size 参数设置.默认是16KB
        //较小的批次大小有可能降低吞吐量（批次大小为0则完全禁用批处理）。
        //比如说，kafka里的消息5秒钟Batch才凑满了16KB，才能发送出去。那这些消息的延迟就是5秒钟
        //实测batchSize这个参数没有用
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProperties.getBatchSize());
        //有的时刻消息比较少,过了很久,比如5min也没有凑够16KB,这样延时就很大,所以需要一个参数. 再设置一个时间,到了这个时间,
        //即使数据没达到16KB,也将这个批次发送出去
        props.put(ProducerConfig.LINGER_MS_CONFIG, "5000");
        //生产者内存缓冲区的大小
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaProperties.getBufferMemory());
        //反序列化，和生产者的序列化方式对应
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
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

    @Bean
    public ProducerFactory<Object, Object> producerFactory(KafkaProperties kafkaProperties) {
        DefaultKafkaProducerFactory<Object, Object> factory = new DefaultKafkaProducerFactory<>(producerConfigs(kafkaProperties));
        return factory;
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(producerFactory(kafkaProperties));
    }
}
