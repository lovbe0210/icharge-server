package com.lovbe.icharge.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.dto.ContentDo;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.config.ContentPicksConfigProperties;
import com.lovbe.icharge.dao.PublicContentDao;
import com.lovbe.icharge.service.RecommendPicksService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/29 11:35
 * @Description: MS
 */
@Slf4j
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

    @Override
    public void contentTagExtract() {
        String publishContentIdKey = RedisKeyConstant.getPublishContentIdKey();
        String publishedId = (String) RedisUtil.zsPopMin(publishContentIdKey);
        while (publishedId != null) {
            // 文档id_正文id
            String[] split = publishedId.split(SysConstant.SEPARATOR);
            ContentDo contentDo = contentDao.selectContent(Long.valueOf(split[1]));
            if (contentDo != null && StringUtils.hasLength(contentDo.getContent())) {
                try {
                    JSONObject parseObj = JSONUtil.parseObj(contentDo.getContent());
                    String content = getTextContentValue(parseObj);
                } catch (Exception e) {
                    log.error("[文章标签提取] --- 当前文章内文内容解析异常，publishedId: {}, 提取失败，errorInfo: {}", publishedId, e.toString());
                }
            }
            publishedId = (String) RedisUtil.zsPopMin(publishContentIdKey);
        }
    }

    private String getTextContentValue(JSONObject parseObj) {
        return null;
    }
}
