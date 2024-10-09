package com.lovbe.icharge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.lovbe.icharge.mapper")
public class IchargeContentProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeContentProductApplication.class, args);
    }

}
