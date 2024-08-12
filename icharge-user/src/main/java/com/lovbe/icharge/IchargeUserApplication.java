package com.lovbe.icharge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class IchargeUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeUserApplication.class, args);
    }

}
