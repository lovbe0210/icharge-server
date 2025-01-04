package com.lovbe.icharge.common.config;

import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import jakarta.annotation.Resource;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/2 12:43
 * @Description: es配置类
 */
@ConditionalOnProperty(value = "spring.elasticsearch.enable", havingValue = "true")
public class ElasticsearchConfig {
    @Resource
    ElasticsearchProperties elasticsearchProperties;

    @Bean
    public RestHighLevelClient highLevelClient() {
        String uris = elasticsearchProperties.getUris();
        if (!StringUtils.hasLength(uris)) {
            throw new ServiceException("系统初始化异常，需要先配置elasticsearch相关属性");
        }
        String[] uriList = uris.split(",");
        HttpHost[] hosts = new HttpHost[uriList.length];
        for (int i = 0; i < uriList.length; i++) {
            String[] uriSplit = uriList[i].split(":");
            hosts[i] = new HttpHost(uriSplit[0], Integer.valueOf(uriSplit[1]), "http");
        }
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(hosts));
        return client;
    }
}
