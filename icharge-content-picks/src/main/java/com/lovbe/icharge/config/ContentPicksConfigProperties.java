package com.lovbe.icharge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/29 11:02
 * @Description: MS
 */
@Data
@ConfigurationProperties(prefix = "algorithm.parameters")
public class ContentPicksConfigProperties {
    /**
     * 浏览量权重
     */
    private double viewWeight = 0.2;
    /**
     * 点赞权重
     */
    private double likeWeight = 0.3;
    /**
     * 收藏权重
     */
    private double collectWeight = 0.25;
    /**
     * 评论权重
     */
    private double commentWeight = 0.25;
    /**
     * 时间衰减重力因子,控制衰减的速率 0.5<G<1
     */
    private double G = 0.5;
    /**
     * 文章排行榜计算批量数
     */
    private Integer rankBatchSize = 1000;
    /**
     * 专栏统计数更新批量数
     */
    private Integer columnStatisticBatchSize = 30;
}
