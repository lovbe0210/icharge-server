package com.lovbe.icharge.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/12 0:07
 * @Description: 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "service")
public class ServiceProperties {
    /**
     * 批量获取用户信息，可一次性最多获取多少个
     */
    private int batchSize;
    /**
     * domain过滤，保留一些菜单值和特殊字符
     */
    private Set<String> filterDomain = new HashSet<>();
}
