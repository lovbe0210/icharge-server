package com.lovbe.icharge;

import com.lovbe.icharge.config.ContentPicksConfigProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@EnableConfigurationProperties(ContentPicksConfigProperties.class)
@MapperScan({"com.lovbe.icharge.dao", "com.lovbe.icharge.common.dao"})
public class IchargeContentPicksApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeContentPicksApplication.class, args);
    }

}
