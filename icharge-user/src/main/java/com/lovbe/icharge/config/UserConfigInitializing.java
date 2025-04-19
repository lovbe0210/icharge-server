package com.lovbe.icharge.config;

import com.lovbe.icharge.common.service.CommonService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author: lovbe0210
 * @Date: 2025/4/19 10:08
 * @Description: 系统初始化后进行redis中domain数据初始化
 */
@Component
public class UserConfigInitializing implements CommandLineRunner {
    @Resource
    private CommonService commonService;

    @Override
    public void run(String... args) throws Exception {
        commonService.initDomainCache();
    }
}
