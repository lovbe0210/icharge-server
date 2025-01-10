package com.lovbe.icharge.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.config.ContentPicksConfigProperties;
import com.lovbe.icharge.dao.BrowseHistoryDao;
import com.lovbe.icharge.dao.PublicContentDao;
import com.lovbe.icharge.entity.dto.BrowseHistoryDo;
import com.lovbe.icharge.service.RecommendPicksService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ml.GetRecordsRequest;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
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
    private BrowseHistoryDao browseHistoryDao;
    @Resource
    private CommonService commonService;
    @Resource
    private ContentPicksConfigProperties properties;
    @Resource
    private RestHighLevelClient highLevelClient;

    @Override
    public void articleRankUpdate() {
        // 获取所有文章的统计数
        Integer count = contentDao.selectStatisticCount(SysConstant.TARGET_TYPE_ARTICLE);
        if (count == 0) {
            return;
        }
        int totalPage = PageUtil.totalPage(count, properties.getRankBatchSize());
        Date now = new Date();
        String rankSetKey = RedisKeyConstant.getRankSetKey(SysConstant.TARGET_TYPE_ARTICLE);
        for (int i = 1; i <= totalPage; i++) {
            int offset = (i - 1) * properties.getRankBatchSize();
            List<TargetStatisticDo> statisticList = contentDao.selectArticleStatisticList(SysConstant.TARGET_TYPE_ARTICLE, offset, properties.getRankBatchSize());
            if (CollectionUtils.isEmpty(statisticList)) {
                continue;
            }
            Set<ZSetOperations.TypedTuple<Object>> tupleList = statisticList.stream()
                    .map(statistic -> getRankTypedTuple(statistic, now))
                    .collect(Collectors.toSet());
            RedisUtil.zSetTuple(rankSetKey, tupleList);
        }
    }

    @Override
    public void columnRankUpdate() {
        // 获取所有专栏的统计数
        Integer count = contentDao.selectStatisticCount(SysConstant.TARGET_TYPE_COLUMN);
        if (count == 0) {
            return;
        }
        int totalPage = PageUtil.totalPage(count, properties.getRankBatchSize());
        Date now = new Date();
        String rankSetKey = RedisKeyConstant.getRankSetKey(SysConstant.TARGET_TYPE_COLUMN);
        for (int i = 1; i <= totalPage; i++) {
            int offset = (i - 1) * properties.getRankBatchSize();
            List<TargetStatisticDo> statisticList = contentDao.selectColumnStatisticList(SysConstant.TARGET_TYPE_COLUMN, offset, properties.getRankBatchSize());
            if (CollectionUtils.isEmpty(statisticList)) {
                continue;
            }
            Set<ZSetOperations.TypedTuple<Object>> tupleList = statisticList.stream()
                    .map(statistic -> getRankTypedTuple(statistic, now))
                    .collect(Collectors.toSet());
            RedisUtil.zSetTuple(rankSetKey, tupleList);
        }
    }

    /**
     * @description: 获取计算分数
     * @param: TargetStatisticDo
     * @return: TypedTuple<Object>
     * @author: lovbe0210
     * @date: 2025/1/8 16:14
     */
    private ZSetOperations.TypedTuple<Object> getRankTypedTuple(TargetStatisticDo statistic, Date now) {
        if (statistic.getType() == SysConstant.TARGET_TYPE_COLUMN && statistic.getArticleCount() == 0) {
            // 专栏文章数为0，直接为0
            return ZSetOperations.TypedTuple.of(statistic.getUid(), 0.0);
        }
        // 点赞 0.3 收藏 0.25 浏览 0.2 评论 0.25
        double viewWeight = properties.getViewWeight();
        int viewCount = statistic.getViewCount();
        double likeWeight = properties.getLikeWeight();
        int likeCount = statistic.getLikeCount();
        double collectWeight = properties.getCollectWeight();
        int collectCount = statistic.getCollectCount();
        double commentWeight = properties.getCommentWeight();
        int commentCount = statistic.getCommentCount();
        Date publishTime = statistic.getPublishTime();
        if (publishTime == null) {
            publishTime = new Date(0L);
        }
        double G = properties.getG();

        // 1. 计算权重分数
        double score = likeWeight * Math.log(likeCount + 1) + collectWeight * Math.log(collectCount + 1) +
                viewWeight * Math.log(viewCount + 1) + commentWeight * Math.log(commentCount + 1);
        // 2. 根据权重分数和时间衰减因子得到最终分数
        double sincePublishDay = DateUtil.betweenDay(publishTime, now, false);
        double finalScore = score / Math.pow(sincePublishDay + 2, G);
        ZSetOperations.TypedTuple<Object> longTypedTuple = ZSetOperations.TypedTuple.of(statistic.getUid(), finalScore);
        return longTypedTuple;
    }

    @Override
    public void portraitTagExtraction() {
        // 获取新增阅读记录的用户id
        String lastTimeKey = RedisKeyConstant.getLastPortraitTagTimeKey();
        Date current = new Date();
        List<Long> userIdList = null;
        if (!RedisUtil.hasKey(lastTimeKey)) {
            // 首次计算获取所有历史记录
            userIdList = browseHistoryDao.selectUserListFromHistory(null, SysConstant.TARGET_TYPE_ARTICLE);
        } else {
            Date lastTime = (Date) RedisUtil.get(lastTimeKey);
            userIdList = browseHistoryDao.selectUserListFromHistory(lastTime, SysConstant.TARGET_TYPE_ARTICLE);
        }
        if (CollectionUtils.isEmpty(userIdList)) {
            RedisUtil.set(lastTimeKey, current);
            return;
        }
        List<UserInfoDo> userList = contentDao.selectUserList(userIdList);
        if (CollectionUtils.isEmpty(userList)) {
            RedisUtil.set(lastTimeKey, current);
            return;
        }
        for (UserInfoDo userInfo : userList) {
            updateUserTagExtraction(userInfo);
        }
        // 将执行时间保存到redis
        RedisUtil.set(lastTimeKey, current);
    }

    @Override
    public void columnStatisticUpdate() {
        Integer columnCount = contentDao.selectColumnCount();
        if (columnCount == 0) {
            return;
        }
        int totalPage = PageUtil.totalPage(columnCount, properties.getColumnStatisticBatchSize());
        for (int i = 1; i <= totalPage; i++) {
            int offset = (i - 1) * properties.getColumnStatisticBatchSize();
            List<TargetStatisticDo> statisticList = contentDao.selectColumnArticleList(offset, properties.getColumnStatisticBatchSize());
            if (CollectionUtils.isEmpty(statisticList)) {
                continue;
            }
            List<TargetStatisticDo> collect = statisticList.stream()
                    .collect(Collectors.groupingBy(TargetStatisticDo::getUid))
                    .entrySet().stream()
                    .map(entry -> {
                        TargetStatisticDo statistic = new TargetStatisticDo();
                        statistic.setUid(entry.getKey());
                        statistic.setType(SysConstant.TARGET_TYPE_COLUMN);
                        entry.getValue().forEach(sts -> {
                            statistic.setCommentCount(statistic.getCommentCount() + sts.getCommentCount())
                                    .setLikeCount(statistic.getLikeCount() + sts.getLikeCount())
                                    .setCollectCount(statistic.getCollectCount() + sts.getCollectCount())
                                    .setViewCount(statistic.getViewCount() + sts.getViewCount());
                        });
                        return statistic;
                    }).collect(Collectors.toList());
            contentDao.updateColumnStatistic(collect);
        }
    }

    /**
     * @description: 对每个用户进行人物画像
     * @param: UserInfoDo
     * @return: void
     * @author: lovbe0210
     * @date: 2025/1/5 23:37
     */
    private void updateUserTagExtraction(UserInfoDo userInfo) {
        try {
            StringBuilder tags = new StringBuilder();
            List<BrowseHistoryDo> historyList = browseHistoryDao.selectList(new LambdaQueryWrapper<BrowseHistoryDo>()
                    .eq(BrowseHistoryDo::getTargetType, SysConstant.TARGET_TYPE_ARTICLE)
                    .eq(BrowseHistoryDo::getUserId, userInfo.getUid())
                    .orderByDesc(BrowseHistoryDo::getUpdateTime));
            if (CollectionUtils.isEmpty(historyList)) {
                return;
            }
            if (historyList.size() > 200) {
                historyList = historyList.subList(0, 200);
            }
            // 从elasticsearch中获取所有标签然后进行人物画像
            MultiGetRequest multiGetRequest = new MultiGetRequest();
            Set<Long> targetIdSet = historyList.stream()
                    .peek(history -> {
                        MultiGetRequest.Item item = new MultiGetRequest.Item(SysConstant.ES_INDEX_ARTICLE,
                                String.valueOf(history.getTargetId()))
                                .fetchSourceContext(new FetchSourceContext(true, new String[]{"tags", "category"}, null));
                        multiGetRequest.add(item);
                    })
                    .map(BrowseHistoryDo::getTargetId)
                    .collect(Collectors.toSet());
            MultiGetResponse multiGetResponse = highLevelClient.mget(multiGetRequest, RequestOptions.DEFAULT);
            for (MultiGetItemResponse itemResponse : multiGetResponse) {
                if (!itemResponse.isFailed() && itemResponse.getResponse().isExists()) {
                    String sourceAsString = itemResponse.getResponse().getSourceAsString();
                    ArticleEsEntity esEntity = JsonUtils.parseObject(sourceAsString, ArticleEsEntity.class);
                    if (esEntity != null) {
                        tags.append(esEntity.getTags());
                    }
                }
            }
            if (tags.isEmpty()) {
                log.error("[人物画像标签提取] --- 获取Es文章标签失败，articleIdList: {}", targetIdSet);
                return;
            }
            AIPortraitTagResultDTO portraitTagResult = commonService.sendPortraitTagChat(tags.toString());
            if (portraitTagResult == null || !portraitTagResult.isResult()) {
                return;
            }
            UserEsEntity userEsEntity = new UserEsEntity()
                    .setUid(userInfo.getUid())
                    .setUsername(userInfo.getUsername());
            if (!CollectionUtils.isEmpty(portraitTagResult.getCategory())) {
                userEsEntity.setCategory(StringUtils.collectionToDelimitedString(portraitTagResult.getCategory(), ","));
            }
            if (!CollectionUtils.isEmpty(portraitTagResult.getTags())) {
                userEsEntity.setTags(StringUtils.collectionToDelimitedString(portraitTagResult.getTags(), ","));
            }
            commonService.updateElasticsearchUser(userEsEntity);
        } catch (Exception e) {
            log.error("[人物画像标签提取] --- 请求Es获取数据失败，userId: {}, errorInfo: {}", userInfo.getUid(), e.toString());
        }
    }
}
