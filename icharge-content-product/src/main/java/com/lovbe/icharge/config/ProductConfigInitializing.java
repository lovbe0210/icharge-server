package com.lovbe.icharge.config;

import com.lovbe.icharge.common.service.CommonService;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @Author: lovbe0210
 * @Date: 2025/4/19 10:18
 * @Description: 文章专栏redis中beautifulId初始化
 */
@Component
public class ProductConfigInitializing implements CommandLineRunner, ApplicationRunner, ApplicationListener {
    @Resource
    private CommonService commonService;

    @Override
    public void run(String... args) throws Exception {
        commonService.initBeautifulIdCache();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }
}
