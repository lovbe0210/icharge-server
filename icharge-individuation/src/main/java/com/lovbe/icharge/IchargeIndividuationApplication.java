package com.lovbe.icharge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@MapperScan({"com.lovbe.icharge.dao"})
@SpringBootApplication
public class IchargeIndividuationApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeIndividuationApplication.class, args);
    }

}
