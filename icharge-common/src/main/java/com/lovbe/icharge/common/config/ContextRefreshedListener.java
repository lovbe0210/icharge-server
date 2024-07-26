package com.lovbe.icharge.common.config;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @description: 服务启动成功之后进行以下工作：...
 * @author: Lvhl
 * @date: 2021/10/12 20:40
 */
@Component
@Slf4j
public class ContextRefreshedListener implements ApplicationRunner {
    @Value("${global.param.snowflakeId}")
    private int snowflakeId;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. 初始化雪花id生成器的单例bean
        IdUtil.getSnowflake(snowflakeId);
    }
}
