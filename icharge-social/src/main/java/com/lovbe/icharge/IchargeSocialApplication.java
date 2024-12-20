package com.lovbe.icharge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@MapperScan({"com.lovbe.icharge.dao", "com.lovbe.icharge.common.dao"})
@SpringBootApplication
public class IchargeSocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeSocialApplication.class, args);
    }

}
