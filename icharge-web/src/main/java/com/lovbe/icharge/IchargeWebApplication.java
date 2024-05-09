package com.lovbe.icharge;

import com.lovbe.icharge.storage.config.OssStorageConfig;
import com.lovbe.icharge.storage.config.OssStorageFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IchargeWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(IchargeWebApplication.class, args);
        OssStorageConfig config = OssStorageFactory.getConfig();
        System.out.println(config);
    }

}
