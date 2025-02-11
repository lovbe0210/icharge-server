package com.lovbe.icharge.common.factory;

import com.lovbe.icharge.common.util.SpringContextUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/11 20:27
 * @Description: kafka消费者工厂，实现自定义获取消息
 */
public class KafkaConsumeFactory {
    // 批次超时时间 10秒
    private static final long BATCH_TIMEOUT_MS = 5 * 1000;

    public static List<ConsumerRecord> pollMessage(String groupId, String topic, int batchSize) {
        // 创建消费者
        DefaultKafkaConsumerFactory consumerFactory = SpringContextUtils.getBean(DefaultKafkaConsumerFactory.class);
        if (consumerFactory == null) {
            return List.of();
        }
        Consumer consumer = consumerFactory.createConsumer(groupId, null);
        consumer.subscribe(Collections.singletonList(topic));
        List<ConsumerRecord> batch = new ArrayList<>(batchSize);
        long batchStartTime = System.currentTimeMillis();
        try {
            while (batch.size() < batchSize && (System.currentTimeMillis() - batchStartTime) < BATCH_TIMEOUT_MS) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                if (records.isEmpty()) {
                    // 无新消息且已超时，提前退出
                    if ((System.currentTimeMillis() - batchStartTime) >= BATCH_TIMEOUT_MS) {
                        break;
                    }
                    continue;
                }

                for (ConsumerRecord record : records) {
                    batch.add(record);
                }
            }
        } finally {
            if (batch.size() > 0) {
                consumer.commitSync(); // 提交 Offset
            }
            consumer.close();
        }
        return batch;
    }
}
