package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.model.vo.DirNodeVo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.dao.BrowseHistoryDao;
import com.lovbe.icharge.dao.CollectDao;
import com.lovbe.icharge.dao.PublicContentDao;
import com.lovbe.icharge.entity.dto.BrowseHistoryDo;
import com.lovbe.icharge.entity.dto.CollectDo;
import com.lovbe.icharge.entity.dto.GlobalSearchDTO;
import com.lovbe.icharge.entity.dto.RecommendRequestDTO;
import com.lovbe.icharge.entity.vo.*;
import com.lovbe.icharge.service.PublicContentService;
import com.lovbe.icharge.service.PublicSearchService;
import com.lovbe.icharge.service.feign.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 0:32
 * @Description: MS
 */
@Slf4j
@Service
public class PublicSearchServiceImpl implements PublicSearchService {
    @Resource
    private PublicContentDao publicContentDao;
    @Resource
    private CommonService commonService;
    @Resource
    private RestHighLevelClient highLevelClient;

    @Override
    public SearchResultVo getGlobalSearchResult(GlobalSearchDTO data, Long userId) {
        SearchResultVo searchResult = new SearchResultVo();
        // 通过elasticsearch进行搜索id然后去数据库查询明细
        getSearchArticleResult(data, searchResult);
        // 通过elasticsearch搜索专栏信息
        getSearchColumnResult(data, searchResult);
        // 通过elasticsearch搜索用户信息
        getSearchUserResult(data, searchResult);
        return searchResult;
    }

    /**
     * @description: 获取搜索用户
     * @param: data
     * @param: searchResult
     * @author: lovbe0210
     * @date: 2025/1/16 20:26
     */
    private void getSearchUserResult(GlobalSearchDTO data, SearchResultVo searchResult) {
        SearchRequest searchRequest = new SearchRequest(SysConstant.ES_INDEX_USER);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 设置字段分词匹配
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 添加关键词匹配 用户名
        boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_USERNAME, data.getKeywords()));
        boolQuery.minimumShouldMatch(1);
        searchSourceBuilder.query(boolQuery);
        // 只获取id字段
        searchSourceBuilder.fetchSource(new String[]{"uid", "username"}, null);
        // 设置高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(SysConstant.ES_FILED_USERNAME).fragmentSize(0).numOfFragments(0);
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<span style=\"color: red\">");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // 添加分页参数
        searchSourceBuilder.from(data.getOffset());
        searchSourceBuilder.size(data.getLimit());
        searchRequest.source(searchSourceBuilder);
        // 发送请求并处理响应
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = response.getHits();
            if (searchHits != null && searchHits.getTotalHits().value == 0) {
                searchResult.setSearchUserCount(0)
                        .setSearchUserList(List.of());
            } else if (searchHits != null && searchHits.getHits().length == 0){
                searchResult.setSearchUserCount(Math.toIntExact(searchHits.getTotalHits().value))
                        .setSearchUserList(List.of());
            } else if (searchHits != null && searchHits.getHits().length != 0) {
                // 构造搜索数据
                List<ExcellentUserVo> userList = Arrays.stream(searchHits.getHits())
                        .map(hit -> {
                            UserInfoDo userInfo = commonService.getCacheUser(Long.valueOf(hit.getId()));
                            ExcellentUserVo userVo = new ExcellentUserVo();
                            BeanUtils.copyProperties(userInfo, userVo);
                            if (userInfo.getUid() == null || !CommonStatusEnum.isNormal(userInfo.getStatus())) {
                                return null;
                            }
                            if (!CollectionUtils.isEmpty(hit.getHighlightFields())) {
                                // 替换高亮红
                                hit.getHighlightFields().values().forEach(highlightField -> {
                                    String highLightValue = StringUtils.arrayToDelimitedString(highlightField.getFragments(), "");
                                    if (SysConstant.ES_FILED_USERNAME.equals(highlightField.getName())) {
                                        userVo.setShowUsername(highLightValue);
                                    }
                                });
                            }
                            return userVo;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(userList)) {
                    searchResult.setSearchUserCount(Math.toIntExact(searchHits.getTotalHits().value))
                            .setSearchUserList(List.of());
                } else {
                    searchResult.setSearchUserCount(Math.toIntExact(searchHits.getTotalHits().value))
                            .setSearchUserList(userList);
                }
            }
        } catch (IOException e) {
            log.error("[获取搜索用户] --- 查询异常，errorInfo: {}", e.toString());
        }
    }

    /**
     * @description: 获取搜索用户
     * @param: data
     * @param: searchResult
     * @author: lovbe0210
     * @date: 2025/1/16 20:26
     */
    private void getSearchColumnResult(GlobalSearchDTO data, SearchResultVo searchResult) {
        SearchRequest searchRequest = new SearchRequest(SysConstant.ES_INDEX_COLUMN);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 设置字段分词匹配
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 过滤查询公开专栏
        boolQuery.filter(QueryBuilders.termQuery(SysConstant.ES_FILED_PUBLIC, 1));
        // 添加关键词匹配 用户名
        boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_TITLE, data.getKeywords()).boost(1.0F));
        boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_SYNOPSIS, data.getKeywords()).boost(0.8F));
        boolQuery.minimumShouldMatch(1);
        searchSourceBuilder.query(boolQuery);
        // 只获取uid, title, synopsis字段
        searchSourceBuilder.fetchSource(new String[]{SysConstant.ES_FILED_UID, SysConstant.ES_FILED_TITLE, SysConstant.ES_FILED_SYNOPSIS}, null);
        // 设置高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(SysConstant.ES_FILED_TITLE).fragmentSize(0).numOfFragments(0);
        highlightBuilder.field(SysConstant.ES_FILED_SYNOPSIS).fragmentSize(0).numOfFragments(0);
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<span style=\"color: red\">");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // 添加分页参数
        searchSourceBuilder.from(data.getOffset());
        searchSourceBuilder.size(data.getLimit());
        searchRequest.source(searchSourceBuilder);
        // 发送请求并处理响应
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = response.getHits();
            if (searchHits != null && searchHits.getTotalHits().value == 0) {
                searchResult.setSearchColumnCount(0)
                        .setSearchColumnList(List.of());
            } else if (searchHits != null && searchHits.getHits().length == 0){
                searchResult.setSearchColumnCount(Math.toIntExact(searchHits.getTotalHits().value))
                        .setSearchColumnList(List.of());
            } else if (searchHits != null && searchHits.getHits().length != 0) {
                // 构造搜索数据
                Map<Long, RecommendColumnVo> columnMap = Arrays.stream(searchHits.getHits())
                        .map(hit -> {
                            RecommendColumnVo columnVo = new RecommendColumnVo();
                            columnVo.setUid(Long.valueOf(hit.getId()));
                            if (!CollectionUtils.isEmpty(hit.getHighlightFields())) {
                                // 替换高亮红
                                hit.getHighlightFields().values().forEach(highlightField -> {
                                    String highLightValue = StringUtils.arrayToDelimitedString(highlightField.getFragments(), "");
                                    if (SysConstant.ES_FILED_TITLE.equals(highlightField.getName())) {
                                        columnVo.setHighLightTitle(highLightValue);
                                    }
                                    if (SysConstant.ES_FILED_SYNOPSIS.equals(highlightField.getName())) {
                                        columnVo.setHighLightSynopsis(highLightValue);
                                    }
                                });
                            }
                            return columnVo;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(RecommendColumnVo::getUid, Function.identity(), (a, b) -> b));
                if (CollectionUtils.isEmpty(columnMap)) {
                    searchResult.setSearchColumnCount(Math.toIntExact(searchHits.getTotalHits().value))
                            .setSearchColumnList(List.of());
                } else {
                    // 获取专栏信息
                    List<RecommendColumnVo> columnList = publicContentDao.selectPublicColumnList(columnMap.keySet());
                    if (!CollectionUtils.isEmpty(columnList)) {
                        columnList.forEach(column -> {
                            RecommendColumnVo columnVo = columnMap.get(column.getUid());
                            if (columnVo != null && columnVo.getHighLightTitle() != null) {
                                column.setHighLightTitle(columnVo.getHighLightTitle());
                            }
                            if (columnVo != null && columnVo.getHighLightSynopsis() != null) {
                                column.setHighLightSynopsis(columnVo.getHighLightSynopsis());
                            }
                        });
                    }
                    searchResult.setSearchColumnCount(Math.toIntExact(searchHits.getTotalHits().value))
                            .setSearchColumnList(columnList);
                }
            }
        } catch (IOException e) {
            log.error("[获取搜索用户] --- 查询异常，errorInfo: {}", e.toString());
        }
    }

    /**
     * @description: 获取文章搜索
     * @param: data
     * @param: searchResult
     * @author: lovbe0210
     * @date: 2025/1/16 13:43
     */
    private void getSearchArticleResult(GlobalSearchDTO data, SearchResultVo searchResult) {
        SearchRequest searchRequest = new SearchRequest(SysConstant.ES_INDEX_ARTICLE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 设置字段分词匹配
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 添加isPublic过滤公开内容,content不为null过滤已发布内容
        boolQuery.filter(QueryBuilders.termQuery(SysConstant.ES_FILED_PUBLIC, 1))
                .filter(QueryBuilders.existsQuery(SysConstant.ES_FILED_CONTENT));
        // 添加关键词匹配 标题、文章摘要、文章内容
        boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_TITLE, data.getKeywords()).boost(1.0F));
        boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_SUMMARY, data.getKeywords()).boost(0.8F));
        boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_CONTENT, data.getKeywords()).boost(0.6F));
        boolQuery.minimumShouldMatch(1);
        searchSourceBuilder.query(boolQuery);
        // 只获取id字段
        searchSourceBuilder.fetchSource(new String[]{SysConstant.ES_FILED_UID, SysConstant.ES_FILED_TITLE, SysConstant.ES_FILED_SUMMARY}, null);
        // 设置高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").fragmentSize(0).numOfFragments(0);
        highlightBuilder.field("summary").fragmentSize(0).numOfFragments(0);
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<span style=\"color: red\">");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // 添加分页参数
        searchSourceBuilder.from(data.getOffset());
        searchSourceBuilder.size(data.getLimit());
        searchRequest.source(searchSourceBuilder);
        // 发送请求并处理响应
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = response.getHits();
            if (searchHits != null && searchHits.getTotalHits().value == 0) {
                searchResult.setSearchArticleCount(0)
                        .setSearchArticleList(List.of());
            } else if (searchHits != null && searchHits.getHits().length == 0){
                searchResult.setSearchArticleCount(Math.toIntExact(searchHits.getTotalHits().value))
                        .setSearchArticleList(List.of());
            } else if (searchHits != null && searchHits.getHits().length != 0) {
                // 构造搜索数据
                List<Long> articleIds = new ArrayList<>();
                Map<Long, FeaturedArticleVo> articleMap = Arrays.stream(searchHits.getHits())
                        .map(hit -> {
                            FeaturedArticleVo articleVo = new FeaturedArticleVo();
                            articleVo.setUid(Long.valueOf(hit.getId()));
                            articleIds.add(Long.valueOf(hit.getId()));
                            if (!CollectionUtils.isEmpty(hit.getHighlightFields())) {
                                // 替换高亮红
                                hit.getHighlightFields().values().forEach(highlightField -> {
                                    String highLightValue = StringUtils.arrayToDelimitedString(highlightField.getFragments(), "");
                                    if (SysConstant.ES_FILED_TITLE.equals(highlightField.getName())) {
                                        articleVo.setTitle(highLightValue);
                                    }
                                    if (SysConstant.ES_FILED_SUMMARY.equals(highlightField.getName())) {
                                        articleVo.setSummary(highLightValue);
                                    }
                                });
                            }
                            return articleVo;
                        })
                        .collect(Collectors.toMap(FeaturedArticleVo::getUid, Function.identity(), (a, b) -> b));
                // 获取文章详细信息
                List<FeaturedArticleVo> articleVoList = publicContentDao.selectPublicArticleList(articleIds);
                if (CollectionUtils.isEmpty(articleVoList)) {
                    // 用户信息填充
                    for (FeaturedArticleVo article : articleVoList) {
                        article.setUserInfo(commonService.getCacheUser(article.getUserInfo().getUid()));
                        // TODO 关注情况

                    }
                    searchResult.setSearchArticleCount(Math.toIntExact(searchHits.getTotalHits().value))
                            .setSearchArticleList(List.of());
                    return;
                }
                // 替换高亮字段
                articleVoList.stream().forEach(article -> {
                    FeaturedArticleVo highLightArticle = articleMap.get(article.getUid());
                    if (highLightArticle != null && highLightArticle.getTitle() != null) {
                        article.setTitle(highLightArticle.getTitle());
                    }
                    if (highLightArticle != null && highLightArticle.getSummary() != null) {
                        article.setSummary(highLightArticle.getSummary());
                    }
                });
                searchResult.setSearchArticleCount(Math.toIntExact(searchHits.getTotalHits().value))
                        .setSearchArticleList(articleVoList);
            }
        } catch (IOException e) {
            log.error("[获取搜索文章] --- 查询异常，errorInfo: {}", e.toString());
        }
    }

    @Override
    public List<FeaturedArticleVo> getScopeSearchResult(GlobalSearchDTO data, Long userId) {
        List<FeaturedArticleVo> articleList = publicContentDao.selectArticleListByTarget(data, userId);
        if (CollectionUtils.isEmpty(articleList)) {
            return List.of();
        }
        Map<Long, FeaturedArticleVo> articleMap = articleList.stream()
                .collect(Collectors.toMap(FeaturedArticleVo::getUid, Function.identity(), (a, b) -> b));
        // 通过elasticsearch进行搜索文章然后获取查询详情
        SearchRequest searchRequest = new SearchRequest(SysConstant.ES_INDEX_ARTICLE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 设置字段分词匹配
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 添加uid直接过滤
        boolQuery.filter(QueryBuilders.termsQuery(SysConstant.ES_FILED_UID, articleMap.keySet()));
        // 添加关键词匹配 标题、用户标签、文章摘要、文章内容
        boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_TITLE, data.getKeywords()).boost(1.0F));
        boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_SUMMARY, data.getKeywords()).boost(0.8F));
        boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_CONTENT, data.getKeywords()).boost(0.6F));
        boolQuery.minimumShouldMatch(1);
        log.info("[es搜索] --- 查询语句：{}", boolQuery);
        searchSourceBuilder.query(boolQuery);
        // 只获取id字段
        searchSourceBuilder.fetchSource(new String[]{"uid", "title", "summary"}, null);
        // 设置高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").fragmentSize(0).numOfFragments(0);
        highlightBuilder.field("summary").fragmentSize(0).numOfFragments(0);
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<span style=\"color: red\">");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);
        // 发送请求并处理响应
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = response.getHits();
            if (searchHits != null && searchHits.getHits().length == 0) {
                // 搜索结果为空
                return List.of();
            }
            return Arrays.stream(searchHits.getHits())
                    .map(hit -> {
                        Long uid = Long.valueOf(hit.getId());
                        FeaturedArticleVo articleVo = articleMap.get(uid);
                        if (articleVo != null && !CollectionUtils.isEmpty(hit.getHighlightFields())) {
                            // 替换高亮红
                            hit.getHighlightFields().values().forEach(highlightField -> {
                                String highLightValue = StringUtils.arrayToDelimitedString(highlightField.getFragments(), "");
                                if (SysConstant.ES_FILED_TITLE.equals(highlightField.getName())) {
                                    articleVo.setHighLightTitle(highLightValue);
                                }
                                if (SysConstant.ES_FILED_SUMMARY.equals(highlightField.getName())) {
                                    articleVo.setHighLightSummary(highLightValue);
                                }
                            });
                        }
                        return articleVo;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("[获取搜索文章] --- 查询异常，errorInfo: {}", e.toString());
            return List.of();
        }
    }

    @Override
    public List<ExcellentUserVo> getGlobalSearchUserList(GlobalSearchDTO data, Long userId) {
        SearchResultVo searchResult = new SearchResultVo();
        getSearchUserResult(data, searchResult);
        return searchResult.getSearchUserList();
    }

    @Override
    public List<RecommendColumnVo> getGlobalSearchColumnList(GlobalSearchDTO data, Long userId) {
        SearchResultVo searchResult = new SearchResultVo();
        getSearchColumnResult(data, searchResult);
        return searchResult.getSearchColumnList();
    }

    @Override
    public List<FeaturedArticleVo> getGlobalSearchArticleList(GlobalSearchDTO data, Long userId) {
        SearchResultVo searchResult = new SearchResultVo();
        getSearchArticleResult(data, searchResult);
        return searchResult.getSearchArticleList();
    }
}
