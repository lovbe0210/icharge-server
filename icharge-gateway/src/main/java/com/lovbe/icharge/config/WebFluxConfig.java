package com.lovbe.icharge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/24 17:13
 * @Description: 请求体大小配置
 */
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {
    /**
     * 默认是256k，如果请求体太大会报
     * （org.springframework.core.io.buffer.DataBufferLimitException:
     * Exceeded limit on max bytes to buffer : 262144 springboot）
     * <p/>
     * 设置为-1，则不显示请求大小，我这里设置为100MB，够用
     * <p/>
     * 配置HTTP消息编解码器，设置最大内存大小为 100MB
     *
     * @param configurer 用于配置HTTP消息编解码器的ServerCodecConfigurer对象
     */
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024);
    }
}
