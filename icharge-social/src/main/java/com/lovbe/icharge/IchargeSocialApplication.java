package com.lovbe.icharge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.lovbe.icharge.dao")
@SpringBootApplication
public class IchargeSocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeSocialApplication.class, args);
    }

}
