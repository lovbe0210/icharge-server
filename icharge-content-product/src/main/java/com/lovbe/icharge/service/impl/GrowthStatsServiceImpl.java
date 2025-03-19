package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.db.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.ColumnDo;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.dao.ArticleDao;
import com.lovbe.icharge.dao.ColumnDao;
import com.lovbe.icharge.dao.CreationIndexDao;
import com.lovbe.icharge.dao.GrowthStatsDao;
import com.lovbe.icharge.entity.dto.CreationIndexDo;
import com.lovbe.icharge.entity.dto.GrowthStatsDo;
import com.lovbe.icharge.entity.vo.ColumnVo;
import com.lovbe.icharge.entity.vo.StateDayLabelVo;
import com.lovbe.icharge.entity.vo.StateMonthLabelVo;
import com.lovbe.icharge.service.GrowthStatsService;
import com.lovbe.icharge.service.feign.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/15 11:44
 * @Description: MS
 */
@Service
public class GrowthStatsServiceImpl implements GrowthStatsService {
    @Resource
    private UserService userService;
    @Resource
    private ArticleDao articleDao;
    @Resource
    private CreationIndexDao creationIndexDao;
    @Resource
    private GrowthStatsDao growthStatsDao;
    @Resource
    private ColumnDao columnDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void creationStatistic() {
        boolean hasMore = true;
        int pageSize = 200;
        int pageNumber = 0;
        HashMap<String, GrowthStatsDo> statisticMap = new HashMap<>();
        while (hasMore) {
            ResponseBean<PageBean<UserInfoDo>> responseBean = userService.getUserInfoList(new BaseRequest<>(Page.of(pageNumber++, pageSize)));
            if (responseBean != null && responseBean.isResult()) {
                List<UserInfoDo> list = responseBean.getData().getList();
                list = CollectionUtils.isEmpty(list) ? List.of() : list;
                Set<Long> userIds = list.stream().map(UserInfoDo::getUid).collect(Collectors.toSet());
                if (!CollectionUtils.isEmpty(userIds)) {
                    growthStatsDao.delete(new LambdaQueryWrapper<GrowthStatsDo>()
                            .in(GrowthStatsDo::getUserId, userIds));
                }
                // 获取创作天数
                List<GrowthStatsDo> selecteList = growthStatsDao.selectCreationCount(userIds);
                if (!CollectionUtils.isEmpty(selecteList)) {
                    for (GrowthStatsDo growthStats : selecteList) {
                        statisticMap.put(growthStats.getUserId() + "_" + growthStats.getRangeType(), growthStats);
                    }
                }
                // 获取文章相关统计数
                List<GrowthStatsDo> articleStatisticList = articleDao.selectArticleCreationStatistic(userIds);
                if (!CollectionUtils.isEmpty(articleStatisticList)) {
                    for (GrowthStatsDo statisticDo : articleStatisticList) {
                        GrowthStatsDo aDo = statisticMap.get(statisticDo.getUserId() + "_" + statisticDo.getRangeType());
                        if (aDo != null) {
                            statisticDo.setCreationDays(aDo.getCreationDays());
                        }
                        statisticMap.put(statisticDo.getUserId() + "_" + statisticDo.getRangeType(), statisticDo);
                    }
                }
                // 获取专栏相关统计数
                List<GrowthStatsDo> columnStatisticList = articleDao.selectColumnCreationStatistic(userIds);
                if (!CollectionUtils.isEmpty(columnStatisticList)) {
                    for (GrowthStatsDo statisticDo : columnStatisticList) {
                        GrowthStatsDo aDo = statisticMap.get(statisticDo.getUserId() + "_" + statisticDo.getRangeType());
                        if (aDo != null) {
                            aDo.setUpdateContents(statisticDo.getUpdateContents() + aDo.getUpdateContents())
                                    .setColumnTotal(statisticDo.getColumnTotal())
                                    .setMostWordsColumnId(statisticDo.getMostWordsColumnId());
                        } else {
                            statisticMap.put(statisticDo.getUserId() + "_" + statisticDo.getRangeType(), statisticDo);
                        }
                    }
                }
                // 获取随笔相关统计数
                List<GrowthStatsDo> essayStatisticList = articleDao.selectEssayCreationStatistic(userIds);
                if (!CollectionUtils.isEmpty(essayStatisticList)) {
                    for (GrowthStatsDo statisticDo : essayStatisticList) {
                        GrowthStatsDo aDo = statisticMap.get(statisticDo.getUserId() + "_" + statisticDo.getRangeType());
                        if (aDo != null) {
                            aDo.setUpdateContents(statisticDo.getUpdateContents() + aDo.getUpdateContents())
                                    .setCreationWords(statisticDo.getCreationWords() + aDo.getCreationWords())
                                    .setEssayTotal(statisticDo.getEssayTotal());
                        } else {
                            statisticMap.put(statisticDo.getUserId() + "_" + statisticDo.getRangeType(), statisticDo);
                        }
                    }
                }
                hasMore = responseBean.getData().isHasMore();
            } else {
                hasMore = false;
            }
            if (statisticMap.size() > 1000) {
                statisticMap.values().forEach(growthStats -> {
                    growthStats.setUid(YitIdHelper.nextId())
                            .setStatus(CommonStatusEnum.NORMAL.getStatus())
                            .setCreateTime(new Date())
                            .setUpdateTime(growthStats.getCreateTime());
                });
                growthStatsDao.insert(statisticMap.values());
                statisticMap.clear();
            }
        }
        if (statisticMap.size() > 0) {
            statisticMap.values().forEach(growthStats -> {
                growthStats.setUid(YitIdHelper.nextId())
                        .setStatus(CommonStatusEnum.NORMAL.getStatus())
                        .setCreateTime(new Date())
                        .setUpdateTime(growthStats.getCreateTime());
            });
            growthStatsDao.insert(statisticMap.values());
        }
    }

    @Override
    public void creationIndex() {
        // 获取前一日更新的文章、专栏和随笔进行活跃度统计
        boolean hasMore = true;
        int pageSize = 200;
        int pageNumber = 0;
        List<CreationIndexDo> creationIndexList = new ArrayList<>();
        while (hasMore) {
            HashMap<Long, TargetStatisticDo> statisticMap = new HashMap<>();
            ResponseBean<PageBean<UserInfoDo>> responseBean = userService.getUserInfoList(new BaseRequest<>(Page.of(pageNumber++, pageSize)));
            if (responseBean != null && responseBean.isResult()) {
                hasMore = responseBean.getData().isHasMore();
                List<UserInfoDo> list = responseBean.getData().getList();
                list = CollectionUtils.isEmpty(list) ? List.of() : list;
                Set<Long> userIds = list.stream().map(UserInfoDo::getUid).collect(Collectors.toSet());
                LocalDate yesterday = LocalDate.now().minusDays(1);
                // 转换为 Date（默认系统时区）
                Date date = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
                if (!CollectionUtils.isEmpty(userIds)) {
                    creationIndexDao.delete(new LambdaQueryWrapper<CreationIndexDo>()
                            .in(CreationIndexDo::getUserId, userIds)
                            .eq(CreationIndexDo::getRecordDate, date));
                }
                // 获取文章更新数量
                List<TargetStatisticDo> articleUpdateCount = articleDao.selectYdUpdateArticleCount(userIds);
                if (!CollectionUtils.isEmpty(articleUpdateCount)) {
                    for (TargetStatisticDo statisticDo : articleUpdateCount) {
                        statisticMap.put(statisticDo.getUserId(), statisticDo);
                    }
                }
                // 获取专栏更新数量
                List<TargetStatisticDo> columnUpdateCount = articleDao.selectYdUpdateColumnCount(userIds);
                if (!CollectionUtils.isEmpty(columnUpdateCount)) {
                    for (TargetStatisticDo statisticDo : columnUpdateCount) {
                        TargetStatisticDo aDo = statisticMap.get(statisticDo.getUserId());
                        if (aDo == null) {
                            statisticMap.put(statisticDo.getUserId(), statisticDo);
                        } else {
                            aDo.setColumnCount(statisticDo.getColumnCount());
                        }
                    }
                }
                // 获取随笔更新数量
                List<TargetStatisticDo> essayUpdateCount = articleDao.selectYdUpdateEssayCount(userIds);
                if (!CollectionUtils.isEmpty(essayUpdateCount)) {
                    for (TargetStatisticDo statisticDo : essayUpdateCount) {
                        TargetStatisticDo aDo = statisticMap.get(statisticDo.getUserId());
                        if (aDo == null) {
                            statisticMap.put(statisticDo.getUserId(), statisticDo);
                        } else {
                            aDo.setEssayCount(statisticDo.getEssayCount());
                        }
                    }
                }
                // 对统计结果进行加权求值
                if (statisticMap.size() == 0) {
                    continue;
                }
                statisticMap.values().stream()
                        .peek(statistic -> {
                            CreationIndexDo aDo = getCreationIndexDo(statistic);
                            creationIndexList.add(aDo);
                        })
                        .collect(Collectors.toList());
            } else {
                hasMore = false;
            }
            if (creationIndexList.size() > 1000) {
                creationIndexDao.insert(creationIndexList);
                creationIndexList.clear();
            }
        }
        if (creationIndexList.size() > 0) {
            creationIndexDao.insert(creationIndexList);
        }
    }

    @Override
    public GrowthStatsDo getGrowthStatList(long userId, Integer rangeType) {
        GrowthStatsDo statsDo = growthStatsDao.selectOne(new LambdaQueryWrapper<GrowthStatsDo>()
                .eq(GrowthStatsDo::getUserId, userId)
                .eq(GrowthStatsDo::getRangeType, rangeType), false);
        if (statsDo != null && statsDo.getMostWordsColumnId() != null) {
            ColumnDo columnDo = columnDao.selectById(statsDo.getMostWordsColumnId());
            if (columnDo != null && CommonStatusEnum.isNormal(columnDo.getStatus())) {
                List<ArticleDo> selectedList = articleDao.selectList(new LambdaQueryWrapper<ArticleDo>()
                        .eq(ArticleDo::getColumnId, columnDo.getUid())
                        .eq(ArticleDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
                if (!CollectionUtils.isEmpty(selectedList)) {
                    statsDo.setMostWordsTitle(columnDo.getTitle());
                    int mostTotalWords = selectedList.stream().mapToInt(ArticleDo::getWordsNum).sum();
                    statsDo.setMostWords(mostTotalWords)
                            .setMostColumnArticle(selectedList.size());
                }
            }
            if (statsDo.getMostWordsTitle() == null && statsDo.getMostWordsArticleId() != null) {
                ArticleDo articleDo = articleDao.selectById(statsDo.getMostWordsArticleId());
                if (articleDo != null) {
                    statsDo.setMostWordsTitle(articleDo.getTitle())
                            .setMostWords(articleDo.getWordsNum());
                }
            }
        }
        return statsDo;
    }

    @Override
    public Map<String, List> getCreationIndexList(String domain) {
        ResponseBean<UserInfoDo> userInfo = userService.getUserInfo(domain);
        if (userInfo == null || !userInfo.isResult()) {
            return Map.of();
        }
        Long userId = userInfo.getData().getUid();
        // 获取起始日期
        LocalDate endDate = LocalDate.now();
        String format = "yyyy-MM-dd";
        int weekOfMonth = endDate.get(WeekFields.ISO.weekOfMonth());
        int monthValue = endDate.getMonthValue();
        int year = endDate.getYear();
        LocalDate startDate = LocalDate.of(year - 1, monthValue, 1)
                .with(WeekFields.ISO.weekOfMonth(), weekOfMonth + 1)
                .with(WeekFields.ISO.dayOfWeek(), 1);
        List<CreationIndexDo> creationIndexList = creationIndexDao.selectList(new LambdaQueryWrapper<CreationIndexDo>()
                .eq(CreationIndexDo::getUserId, userId)
                .ge(CreationIndexDo::getRecordDate, startDate));
        Map<String, CreationIndexDo> creationIndexMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(creationIndexList)) {
            for (CreationIndexDo indexDo : creationIndexList) {
                String date = DateUtil.format(indexDo.getRecordDate(), format);
                creationIndexMap.put(date, indexDo);
            }
        }
        // 从开始日期到结束日期开始按周组转数据
        List<StateMonthLabelVo> monthLabelList = new ArrayList<>();
        List<List<StateDayLabelVo>> yearLabelList = new ArrayList<>();
        List<StateDayLabelVo> weekLabelList = new ArrayList<>();
        while (startDate.compareTo(endDate) <= 0) {
            String date = LocalDateTimeUtil.format(startDate, format);
            CreationIndexDo indexDo = creationIndexMap.get(date);
            String weekDisplayName = CommonUtils.weekday(startDate);
            weekLabelList.add(new StateDayLabelVo(date, weekDisplayName, indexDo == null ? 0 : indexDo.getCreationScore()));
            int dayOfMonth = startDate.getDayOfMonth();
            if (dayOfMonth == 1) {
                monthLabelList.add(new StateMonthLabelVo(startDate.getMonthValue()+"月", yearLabelList.size()));
            }
            if (Objects.equals(startDate.getDayOfWeek().getValue(), DayOfWeek.SUNDAY.getValue())) {
                yearLabelList.add(weekLabelList);
                weekLabelList = new ArrayList<>();
            }
            startDate = startDate.plus(1, ChronoUnit.DAYS);
        }
        if (weekLabelList.size() > 0) {
            yearLabelList.add(weekLabelList);
        }
        Map<String, List> map = Map.of("monthLabel", monthLabelList, "yearLabel", yearLabelList);
        return map;
    }

    /**
     * @description: 计算活跃度
     * @param: statistic
     * @return: com.lovbe.icharge.entity.dto.CreationIndexDo
     * @author: lovbe0210
     * @date: 2025/3/16 11:27
     */
    private static CreationIndexDo getCreationIndexDo(TargetStatisticDo statistic) {
        // 计算加权和（60%x + 10%z + 30%y）
        double weightedSum = statistic.getArticleCount() * 0.6 + statistic.getColumnCount() * 0.1 + statistic.getEssayCount() * 0.3;
        // 将加权和映射到0-5的整数（按照每天5篇文章最大值活跃计算每0.6单位对应1分，四舍五入）
        int t = (int) Math.ceil(weightedSum / 0.6);
        // 确保结果在0-5之间
        int score = Math.min(5, Math.max(0, t));
        Date createTime = new Date();
        long yd = createTime.getTime() - 1000 * 60 * 60 * 24;
        CreationIndexDo aDo = new CreationIndexDo(statistic.getUserId(), score, new Date(yd));
        aDo.setUid(YitIdHelper.nextId())
                .setCreateTime(createTime)
                .setUpdateTime(createTime)
                .setStatus(CommonStatusEnum.NORMAL.getStatus());
        return aDo;
    }
}
