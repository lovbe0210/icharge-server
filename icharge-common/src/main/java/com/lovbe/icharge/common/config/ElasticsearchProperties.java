package com.lovbe.icharge.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/2 16:15
 * @Description: es参数
 */
@Data
@ConfigurationProperties(prefix = "spring.elasticsearch.high-level")
public class ElasticsearchProperties {
    /**
     * es地址，集群实例用,隔开 eg:10.2.2.14:9200,10.2.2.15:9200,10.2.2.16:9200
     */
    private String uris;
}
