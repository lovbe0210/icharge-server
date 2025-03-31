package com.lovbe.icharge.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.EncorageBehaviorEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.model.vo.PublicArticleVo;
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
                    // 统计这里需要过滤一些系统文章
                    .filter(statistic -> !properties.getFilterArticleIds().contains(statistic.getUid()))
                    .map(statistic -> getRankTypedTuple(statistic, now))
                    .collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(tupleList)) {
                continue;
            }
            RedisUtil.zSetTuple(rankSetKey, tupleList);
        }
        // 获取前30名文章设置入选精选
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = RedisUtil.zsGetSet(rankSetKey, 0, 29, true);
        if (CollectionUtils.isEmpty(typedTuples)) {
            return;
        }
        List<Long> articleIds = typedTuples.stream()
                .map(tuple -> (Long) tuple.getValue())
                .collect(Collectors.toList());
        contentDao.updateArticleFeature(articleIds);
        // 查询还未获得精选激励的文章添加电池激励
        List<PublicArticleVo> articleVoList = contentDao.selectNoEncourageList(articleIds, EncorageBehaviorEnum.BEHAVIOR_LIKED.getBehaviorType());
        if (CollectionUtils.isEmpty(articleVoList)) {
            return;
        }
        List<EncourageLogDo> collect = articleVoList.stream()
                .map(article -> {
                    EncourageLogDo encourageLog = new EncourageLogDo()
                            .setUserId(article.getUserId())
                            .setBehaviorType(EncorageBehaviorEnum.BEHAVIOR_FEATURE.getBehaviorType())
                            .setTargetId(article.getUid())
                            .setTargetName(article.getTitle())
                            .setEncourageScore(EncorageBehaviorEnum.BEHAVIOR_FEATURE.getEncourageScore());
                    encourageLog.setUid(YitIdHelper.nextId());
                    return encourageLog;
                })
                .collect(Collectors.toList());
        commonService.saveEncourageLog(collect);
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
                    .filter(statistic -> !properties.getFilterColumnIds().contains(statistic.getUid()))
                    .map(statistic -> getRankTypedTuple(statistic, now))
                    .collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(tupleList)) {
                continue;
            }
            RedisUtil.zSetTuple(rankSetKey, tupleList);
        }
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

    @Override
    public void authorRankUpdate() {
        // 只统计有公开文章和专栏的用户
        Integer count = contentDao.selectUserCount();
        if (count == 0) {
            return;
        }
        int totalPage = PageUtil.totalPage(count, properties.getUserStatisticBatchSize());
        Date now = new Date();
        String rankSetKey = RedisKeyConstant.getRankSetKey(SysConstant.TARGET_TYPE_AUTHOR);
        for (int i = 1; i <= totalPage; i++) {
            int offset = (i - 1) * properties.getUserStatisticBatchSize();
            List<TargetStatisticDo> statisticList = contentDao.selectUserStatisticList(offset, properties.getUserStatisticBatchSize());
            if (CollectionUtils.isEmpty(statisticList)) {
                continue;
            }
            Set<ZSetOperations.TypedTuple<Object>> tupleList = statisticList.stream()
                    // 过滤系统白名单用户
                    .filter(statistic -> !properties.getFilterUserIds().contains(statistic.getUid()))
                    .map(statistic -> getRankTypedTuple(statistic, now))
                    .collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(tupleList)) {
                return;
            }
            RedisUtil.zSetTuple(rankSetKey, tupleList);
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
                    // 过滤系统指定文章
                    .filter(history -> !properties.getFilterArticleIds().contains(history.getTargetId()))
                    .peek(history -> {
                        MultiGetRequest.Item item = new MultiGetRequest.Item(SysConstant.ES_INDEX_ARTICLE,
                                String.valueOf(history.getTargetId()))
                                .fetchSourceContext(new FetchSourceContext(true, new String[]{"tags", "category"}, null));
                        multiGetRequest.add(item);
                    })
                    .map(BrowseHistoryDo::getTargetId)
                    .collect(Collectors.toSet());
            if  (CollectionUtils.isEmpty(multiGetRequest.getItems())) {
                return;
            }
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
            commonService.updateElasticsearchUser(Arrays.asList(userEsEntity));
        } catch (Exception e) {
            log.error("[人物画像标签提取] --- 请求Es获取数据失败，userId: {}, errorInfo: {}", userInfo.getUid(), e.toString());
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
        // 对于创作者或专栏，公开文章数为0，直接分数为0
        if ((statistic.getType() == SysConstant.TARGET_TYPE_COLUMN || statistic.getType() == SysConstant.TARGET_TYPE_AUTHOR)
                && statistic.getArticleCount() == 0) {
            return ZSetOperations.TypedTuple.of(statistic.getUid(), 0.0);
        }

        double score = 0.0;
        double G = 0.0;
        int viewCount = statistic.getViewCount();
        int likeCount = statistic.getLikeCount();
        int collectCount = statistic.getCollectCount();
        int commentCount = statistic.getCommentCount();
        // 1. 计算权重分数
        if (statistic.getType() == SysConstant.TARGET_TYPE_AUTHOR) {
            // 创作者
            double viewWeight = properties.getUserViewWeight();
            double likeWeight = properties.getUserLikeWeight();
            double collectWeight = properties.getUserCollectWeight();
            double commentWeight = properties.getUserCommentWeight();
            double levelWeight = properties.getUserLevelWeight();
            int level = statistic.getUserLevel();
            double articlesWeight = properties.getUserArticlesWeight();
            int articleCount = statistic.getArticleCount();
            double columnsWeight = properties.getUserColumnsWeight();
            int columnCount = statistic.getColumnCount();
            G = properties.getUserG();
            score = levelWeight * Math.log(level + 1) + articlesWeight * Math.log(articleCount + 1) +
                    columnsWeight * Math.log(columnCount + 1) +
                    likeWeight * Math.log(likeCount + 1) + collectWeight * Math.log(collectCount + 1) +
                    viewWeight * Math.log(viewCount + 1) + commentWeight * Math.log(commentCount + 1);
        } else {
            // 文章、专栏
            double viewWeight = properties.getContentViewWeight();
            double likeWeight = properties.getContentLikeWeight();
            double collectWeight = properties.getContentCollectWeight();
            double commentWeight = properties.getContentCommentWeight();
            G = properties.getContentG();
            score = likeWeight * Math.log(likeCount + 1) + collectWeight * Math.log(collectCount + 1) +
                    viewWeight * Math.log(viewCount + 1) + commentWeight * Math.log(commentCount + 1);
        }

        // 2. 根据权重分数和时间衰减因子得到最终分数
        Date publishTime = statistic.getPublishTime();
        if (publishTime == null) {
            publishTime = new Date(0L);
        }
        double sincePublishDay = DateUtil.betweenDay(publishTime, now, false);
        double finalScore = score / Math.pow(sincePublishDay + 2, G);
        ZSetOperations.TypedTuple<Object> longTypedTuple = ZSetOperations.TypedTuple.of(statistic.getUid(), finalScore);
        return longTypedTuple;
    }
}
