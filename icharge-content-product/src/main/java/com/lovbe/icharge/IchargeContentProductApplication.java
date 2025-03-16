package com.lovbe.icharge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@MapperScan({"com.lovbe.icharge.dao", "com.lovbe.icharge.common.dao"})
public class IchargeContentProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeContentProductApplication.class, args);
    }

}
