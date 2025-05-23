package com.lovbe.icharge.schedule;

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
    private RecommendPicksService recommendPicksService;

    /**
     * @description: 计算排行榜 系统启动运行一次，然后每12小时运行一次
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 11:27
     */
    @Scheduled(initialDelay = 10000, fixedRate = 12 * 60 * 60 * 1000)
    public void runArticleRankUpdate() {
        log.info("[文章排行榜] --- 计算开始");
        long start = System.currentTimeMillis();
        recommendPicksService.articleRankUpdate();
        log.info("[文章排行榜] --- 计算结束，耗时：{}ms", System.currentTimeMillis() - start);
    }

    /**
     * @description: 计算排行榜 系统启动运行一次，然后每12小时运行一次
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 11:27
     */
    @Scheduled(initialDelay = 60000, fixedRate = 12 * 60 * 60 * 1000)
    public void runColumnRankUpdate() {
        log.info("[专栏排行榜] --- 计算开始");
        long start = System.currentTimeMillis();
        recommendPicksService.columnRankUpdate();
        log.info("[专栏排行榜] --- 计算结束，耗时：{}ms", System.currentTimeMillis() - start);
    }

    /**
     * @description: 专栏信息统计 系统启动运行一次，然后每1小时运行一次
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 11:27
     */
    @Scheduled(initialDelay = 5000, fixedRate = 60 * 60 * 1000)
    public void runColumnStatistic() {
        log.info("[专栏信息统计] --- 计算开始");
        long start = System.currentTimeMillis();
        recommendPicksService.columnStatisticUpdate();
        log.info("[专栏信息统计] --- 计算结束，耗时：{}ms", System.currentTimeMillis() - start);
    }

    /**
     * @description: 通过阅读记录进行人物画像，提炼推荐标签，间隔6小时
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 11:27
     */
    @Scheduled(initialDelay = 30000, fixedRate = 6 * 60 * 60 * 1000)
    public void runPortraitTagExtract() {
        log.info("[人物画像标签提取] --- 计算开始");
        long start = System.currentTimeMillis();
        recommendPicksService.portraitTagExtraction();
        log.info("[人物画像标签提取] --- 请求结束，耗时：{}ms", System.currentTimeMillis() - start);
    }

    /**
     * @description: 计算排行榜 系统启动运行一次，然后每12小时运行一次
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 11:27
     */
    @Scheduled(initialDelay = 120000, fixedRate = 12 * 60 * 60 * 1000)
    public void runAuthorRankUpdate() {
        log.info("[创作者排行榜] --- 计算开始");
        long start = System.currentTimeMillis();
        recommendPicksService.authorRankUpdate();
        log.info("[创作者排行榜] --- 计算结束，耗时：{}ms", System.currentTimeMillis() - start);
    }
}
