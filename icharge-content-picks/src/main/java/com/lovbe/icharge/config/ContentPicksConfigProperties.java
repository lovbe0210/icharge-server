package com.lovbe.icharge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/29 11:02
 * @Description: MS
 */
@Data
@ConfigurationProperties(prefix = "algorithm.parameters")
public class ContentPicksConfigProperties {
    // --------------------- 内容相关 -------------------------
    /**
     * 浏览量权重
     */
    private double contentViewWeight = 0.25;
    /**
     * 点赞权重
     */
    private double contentLikeWeight = 0.35;
    /**
     * 收藏权重
     */
    private double contentCollectWeight = 0.25;
    /**
     * 评论权重
     */
    private double contentCommentWeight = 0.15;
    /**
     * 时间衰减重力因子,控制衰减的速率 0.5<G<1
     */
    private double contentG = 0.5;

    // --------------------- 用户相关 -------------------------
    /**
     * 用户等级所占权重
     */
    private double userLevelWeight = 0.05;
    /**
     * 文章量所占权重
     */
    private double userArticlesWeight = 0.1;
    /**
     * 专栏量所占权重
     */
    private double userColumnsWeight = 0.05;
    /**
     * 文章总浏览量权重
     */
    private double userViewWeight = 0.2;
    /**
     * 文章总点赞量权重
     */
    private double userLikeWeight = 0.3;
    /**
     * 文章总收藏量权重
     */
    private double userCollectWeight = 0.2;
    /**
     * 文章总评论量权重
     */
    private double userCommentWeight = 0.1;
    /**
     * 时间衰减重力因子,控制衰减的速率 0.5<G<1
     */
    private double userG = 0.5;

    /**
     * 文章排行榜计算批量数
     */
    private Integer rankBatchSize = 1000;
    /**
     * 专栏统计数更新批量数
     */
    private Integer columnStatisticBatchSize = 30;
    /**
     * 用户统计数更新批量数
     */
    private Integer userStatisticBatchSize = 20;
}
