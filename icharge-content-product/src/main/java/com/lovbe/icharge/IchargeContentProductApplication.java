package com.lovbe.icharge;

import com.lovbe.icharge.storage.config.OssStorageConfig;
import com.lovbe.icharge.storage.config.OssStorageFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IchargeContentProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeContentProductApplication.class, args);
        OssStorageConfig config = OssStorageFactory.getConfig();
        System.out.println(config);
    }

}
