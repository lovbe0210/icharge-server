package com.lovbe.icharge;

import com.lovbe.icharge.storage.config.OssStorageConfig;
import com.lovbe.icharge.storage.config.OssStorageFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IchargeUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeUserApplication.class, args);
        OssStorageConfig config = OssStorageFactory.getConfig();
        System.out.println(config);
    }

}
