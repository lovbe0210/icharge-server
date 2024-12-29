package com.lovbe.icharge.service.impl;

import cn.hutool.core.date.DateUtil;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.config.ContentPicksConfigProperties;
import com.lovbe.icharge.dao.PublicContentDao;
import com.lovbe.icharge.service.RecommendPicksService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/29 11:35
 * @Description: MS
 */
@Service
public class RecommendPicksServiceImpl implements RecommendPicksService {
    @Resource
    private PublicContentDao contentDao;
    @Resource
    private ContentPicksConfigProperties properties;

    @Override
    public void articleRankUpdate() {
        // 获取所有文章的统计参数
        List<TargetStatisticDo> statisticList = contentDao.selectContentStatisticList(SysConstant.TARGET_TYPE_ARTICLE);
        if (CollectionUtils.isEmpty(statisticList)) {
            return;
        }
        Date now = new Date();
        String rankSetKey = RedisKeyConstant.getArticleRankSetKey(SysConstant.TARGET_TYPE_ARTICLE);
        statisticList.stream().peek(statistic -> {
            // 点赞 0.3 收藏 0.25 播放 0.2 评论 0.25
            double viewWeight = properties.getViewWeight();
            int viewCount = statistic.getViewCount();
            double likeWeight = properties.getLikeWeight();
            int likeCount = statistic.getLikeCount();
            double collectWeight = properties.getCollectWeight();
            int collectCount = statistic.getCollectCount();
            double commentWeight = properties.getCommentWeight();
            int commentCount = statistic.getCommentCount();
            Date publishTime = statistic.getPublishTime();
            double G = properties.getG();

            // 1. 计算权重分数
            double score = likeWeight * Math.log(likeCount + 1) + collectWeight * Math.log(collectCount + 1) +
                    viewWeight * Math.log(viewCount + 1) + commentWeight * Math.log(commentCount + 1);
            // 2. 根据权重分数和时间衰减因子得到最终分数
            double sincePublishDay = DateUtil.betweenDay(publishTime, now, false);
            double finalScore = score / Math.pow(sincePublishDay + 2, G);
            RedisUtil.zset(rankSetKey, finalScore, statistic.getUid());
        }).collect(Collectors.toList());
    }
}
