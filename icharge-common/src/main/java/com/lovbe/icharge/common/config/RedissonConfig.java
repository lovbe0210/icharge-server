package com.lovbe.icharge.common.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: Redis配置类
 * @author: Lvhl
 * @date: 2021/11/5 17:08
 */
@Slf4j
@ConditionalOnProperty(name = "spring.data.redis.enable", havingValue = "true", matchIfMissing = true)
@Configuration
public class RedissonConfig {

    /**
     * 初始化Redisson客户端
     */
    @Bean
    public RedissonClient redisson(RedisProperties properties) {
        return getRedissonClient(properties);
    }

    /**
     * description: 创建Redisson客户端
     * @author: Lvhl
     * @date: 2021/11/8 13:14
     * @param properties
     * @return RedissonClient
     */
    public RedissonClient getRedissonClient(RedisProperties properties){
        // 判断当前模式是集群模式还是单机模式
        Config config = new Config();
        if (properties.getSentinel() != null) {
            // sentinel哨兵模式
            RedisProperties.Sentinel sentinel = properties.getSentinel();
            List<String> nodes = sentinel.getNodes();
            if (!CollectionUtils.isEmpty(nodes)) {
                nodes = nodes.stream()
                        .map(addr -> "redis://" + addr)
                        .collect(Collectors.toList());
            }
            config.useSentinelServers()
                    .setPassword(properties.getPassword())
                    .setMasterName(sentinel.getMaster())
                    .setDatabase(properties.getDatabase())
                    .setSentinelAddresses(nodes);
        } else if (properties.getCluster() != null) {
            // 集群模式
            RedisProperties.Cluster cluster = properties.getCluster();

        } else {
            // 单机模式
            config.useSingleServer()
                    // 设置单节点地址
                    .setAddress(properties.getHost() + ":" + properties.getPort())
                    // 设置连接的数据库
                    .setDatabase(properties.getDatabase())
                    // 密码
                    .setPassword(properties.getPassword());
        }
        config.setCodec(new JsonJacksonCodec());
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
