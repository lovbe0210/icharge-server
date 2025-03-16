package com.lovbe.icharge.schedule;

import com.lovbe.icharge.service.GrowthStatsService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/29 10:57
 * @Description: 用户成长轨迹数据统计
 */
@Slf4j
@Component
public class CreateStatisticSchedule {
    @Resource
    private GrowthStatsService statisticService;

    @PostConstruct
    public void init() {
        // 系统启动后运行一次
        statisticService.creationIndex();
        statisticService.creationStatistic();
    }

    /**
     * @description: 计算统计数据 系统启动运行一次，然后每天05：00运行一次
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 11:27
     */
    @Scheduled(cron = "0 0 05 * * ?")
    public void runCreationStatistic() {
        log.info("[成长轨迹统计] --- 计算开始");
        long start = System.currentTimeMillis();
        statisticService.creationStatistic();
        log.info("[成长轨迹统计] --- 计算结束，耗时：{}ms", System.currentTimeMillis() - start);
    }

    /**
     * @description: 计算创作指数 系统启动运行一次，然后每天04：00运行一次
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 11:27
     */
    @Scheduled(cron = "0 0 04 * * ?")
    public void runCreationIndex() {
        log.info("[创作指数统计] --- 计算开始");
        long start = System.currentTimeMillis();
        statisticService.creationIndex();
        log.info("[创作指数统计] --- 计算结束，耗时：{}ms", System.currentTimeMillis() - start);
    }
}
