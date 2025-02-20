package com.lovbe.icharge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/21 2:01
 * @Description: MS
 */
@Configuration
public class WebSocketContainerConfig {

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // 设置全局空闲超时（单位：毫秒）
        container.setMaxSessionIdleTimeout(30000L);
        return container;
    }
}
