package com.lovbe.icharge;

import com.lovbe.icharge.common.config.RedisConfig;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.service.impl.CommonServiceImpl;
import com.lovbe.icharge.config.OssStorageFactory;
import com.lovbe.icharge.service.OssStorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, RedisAutoConfiguration.class})
public class IchargeStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeStorageApplication.class, args);
    }

}
