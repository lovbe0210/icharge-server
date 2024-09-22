package com.lovbe.icharge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: lovbe0210
 * @Date: 2024/9/22 14:35
 * @Description: MS
 */
@Data
@RefreshScope
@Component
@ConfigurationProperties(prefix = "request.api")
public class GatewayConfigProperties {
    /**
     * api 接口请求白名单
     */
    private Set<String> whiteList = new HashSet<>();
}

