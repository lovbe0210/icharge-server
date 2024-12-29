package com.lovbe.icharge.schedule;

import com.lovbe.icharge.config.ContentPicksConfigProperties;
import com.lovbe.icharge.service.RecommendPicksService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/29 10:57
 * @Description: MS
 */
@Slf4j
@Component
@EnableScheduling
public class RecommendPicksSchedule {
    @Resource
    private ContentPicksConfigProperties configProperties;

    @Resource
    private RecommendPicksService recommendPicksService;

    /**
     * @description: 计算排行榜 系统启动运行一次，然后每12小时运行一次
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 11:27
     */
    @Scheduled(initialDelay = 10000, fixedRate = 12 * 60 * 60 * 1000)
    public void runTask() {
        log.info("[内容排行榜] --- 计算开始");
        long start = System.currentTimeMillis();
        recommendPicksService.articleRankUpdate();
        log.info("[内容排行榜] --- 计算结束，耗时：{}ms", System.currentTimeMillis() - start);
    }
}
