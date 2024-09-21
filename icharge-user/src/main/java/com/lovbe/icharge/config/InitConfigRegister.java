package com.lovbe.icharge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author: Lvhl
 * @Date: 2024/9/21 11:39
 * @Description: 初始化配置注册器
 */
@Configuration
public class InitConfigRegister {

    @Bean
    public BCryptPasswordEncoder cryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
