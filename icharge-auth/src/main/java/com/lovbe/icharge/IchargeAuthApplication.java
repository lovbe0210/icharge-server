package com.lovbe.icharge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class IchargeAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeAuthApplication.class, args);
    }

}
