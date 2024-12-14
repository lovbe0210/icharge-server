package com.lovbe.icharge.service;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/14 22:18
 * @Description: 定时任务
 */
@EnableScheduling
@Component
public class TaskScheduleService {
    /**
     * @description: 矫正缓存中的点赞明细
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/14 22:22
     */
//    @Scheduled(cron = "10 * * * * *")
    public void correctsLikeDetail() {
        // TODO
    }
}
