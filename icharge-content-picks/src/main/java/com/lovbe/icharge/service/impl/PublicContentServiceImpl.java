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
import com.lovbe.icharge.common.model.base.KafkaMessage;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.model.vo.DirNodeVo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.dao.BrowseHistoryDao;
import com.lovbe.icharge.dao.CollectDao;
import com.lovbe.icharge.dao.PublicContentDao;
import com.lovbe.icharge.entity.dto.BrowseHistoryDo;
import com.lovbe.icharge.entity.dto.CollectDo;
import com.lovbe.icharge.entity.dto.RecommendRequestDTO;
import com.lovbe.icharge.entity.vo.*;
import com.lovbe.icharge.service.PublicContentService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 0:32
 * @Description: MS
 */
@Slf4j
@Service
public class PublicContentServiceImpl implements PublicContentService {
    @Resource
    private PublicContentDao publicContentDao;
    @Resource
    private BrowseHistoryDao browseHistoryDao;
    @Resource
    private UserService userService;
    @Resource
    private CommonService commonService;
    @Resource
    private CollectDao collectDao;
    @Resource
    private RestHighLevelClient highLevelClient;
    // 文档，专栏，随笔，阅读
    @Value("${spring.kafka.topics.user-action-browse}")
    private String browseActionTopic;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public PublicArticleVo getArticleInfo(String articleUri, Long userId) {
        // 如果userId和文档所在userId相同，则使用最新的contentId，否则使用已发布的contentId
        ArticleDo articleDo = publicContentDao.selectArticleInfo(articleUri, userId);
        if (articleDo == null) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }

        PublicArticleVo articleVo = new PublicArticleVo();
        BeanUtil.copyProperties(articleDo, articleVo);
        // 已登陆查询点赞记录
        if (userId != null) {
            String likesSetKey = RedisKeyConstant.getUserLikesSet(userId);
            boolean hasValue = RedisUtil.zsHasValue(likesSetKey, articleDo.getUid());
            articleVo.setIfLike(hasValue);
        }
        // 查询点赞列表
        String likedSetKey = RedisKeyConstant.getTargetLikedSet(articleDo.getUid());
        Set<ZSetOperations.TypedTuple<Object>> tupleList = RedisUtil.zsGetSet(likedSetKey, 0, 13, true);
        if (!CollectionUtils.isEmpty(tupleList)) {
            List<Object> userIdList = new ArrayList<>();
            for (ZSetOperations.TypedTuple<Object> tuple : tupleList) {
                userIdList.add(tuple.getValue());
            }
            HashMap<Long, UserInfoDo> userMap = new HashMap<>();
            ResponseBean<List<UserInfoDo>> userResp = userService.getUserInfoList(new BaseRequest<>(Map.of("userIdList", userIdList)));
            if (userResp != null && !CollectionUtils.isEmpty(userResp.getData())) {
                Map<Long, UserInfoDo> collect = userResp.getData().stream()
                        .collect(Collectors.toMap(UserInfoDo::getUid, Function.identity()));
                userMap.putAll(collect);
            }
            List<UserInfoDo> collect = userIdList.stream()
                    .map(uid -> {
                        UserInfoDo userInfo = CommonUtils.checkUserStatus(userMap.get(uid));
                        userInfo.setUid((Long) uid);
                        return userInfo;
                    })
                    .collect(Collectors.toList());
            articleVo.setLikeUserList(collect);
        } else {
            articleVo.setLikeUserList(List.of());
        }

        // 查询文章内容
        if (Objects.equals(articleDo.getUserId(), userId)) {
            // 作者本人，取最新内容版本id
            Long latestContentId = articleDo.getLatestContentId();
            if (latestContentId != null) {
                ContentDo contentDo = publicContentDao.selectContent(latestContentId);
                if (contentDo != null) {
                    articleVo.setContent(contentDo.getContent());
                }
            }
            return articleVo;
        }
        // 非作者阅读或作者未登录阅读
        if (articleDo.getIsPublic() == 0 || articleDo.getPublishedContentId() == null) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        ContentDo contentDo = publicContentDao.selectContent(articleDo.getPublishedContentId());
        if (contentDo == null) {
            return articleVo;
        }
        articleVo.setContent(contentDo.getContent());
        return articleVo;
    }

    @Override
    public RouterInfoVo getRouterDirection(String dynamicId) {
        RouterInfoVo routerInfo = publicContentDao.selectUriType(dynamicId);
        return routerInfo;
    }

    @Override
    public List<PublicArticleVo> getArticleList(Long authorId, Long userId) {
        List<ArticleDo> articleList = publicContentDao.selectArticleListByUserId(authorId);
        if (CollectionUtils.isEmpty(articleList)) {
            return Collections.EMPTY_LIST;
        }
        List<PublicArticleVo> result = new ArrayList<>();
        for (ArticleDo articleDo : articleList) {
            Integer isPublic = articleDo.getIsPublic();
            Long publishedContentId = articleDo.getPublishedContentId();
            if (!Objects.equals(authorId, userId) && (isPublic == 0 || publishedContentId == null)) {
                continue;
            }
            PublicArticleVo articleVo = new PublicArticleVo();
            BeanUtil.copyProperties(articleDo, articleVo);
            result.add(articleVo);
        }
        return result;
    }

    @Override
    public PublicColumnVo getColumnDir(String uri, Long userId) {
        /*
            1. 只获取组装目录不更新目录，
            2. 如果没有目录，直接组装一个平铺的list，
            3. 如果有目录，判断是否是作者本人，
                4. 如果不是作者需要过滤目录中的文章是存在且有已发布的内容
                5. 如果是作者本人无需过滤，只需要保证文章标题是最新的即可
         */
        ColumnDo columnDo = publicContentDao.selectColumnInfo(uri);
        if (columnDo == null) {
            throw new ServiceException(ServiceErrorCodes.COLUMN_NOT_EXIST);
        }
        int isPublic = columnDo.getIsPublic();
        Long authorId = columnDo.getUserId();
        if (!Objects.equals(authorId, userId) && isPublic == 0) {
            throw new ServiceException(ServiceErrorCodes.COLUMN_NOT_PUBLIC);
        }
        PublicColumnVo columnVo = new PublicColumnVo();
        BeanUtil.copyProperties(columnDo, columnVo);
        Long dirContentId = columnDo.getDirContentId();
        List<ArticleDo> articleList = columnDo.getArticleList();
        // 目录id为空
        if (dirContentId == null && CollectionUtils.isEmpty(articleList)) {
            return columnVo;
        }
        if (dirContentId == null && !CollectionUtils.isEmpty(articleList)) {
            List<DirNodeVo> collect = articleList.stream()
                    .filter(articleDo -> Objects.equals(authorId, userId) ? true : articleDo.getPublishedContentId() != null)
                    .sorted((o1, o2) -> o1.getUpdateTime().compareTo(o2.getUpdateTime()))
                    .map(article -> {
                        DirNodeVo node = new DirNodeVo()
                                .setUid(article.getUid())
                                .setUri(article.getUri())
                                .setType(1)
                                .setTitle(article.getTitle())
                                .setSummary(article.getSummary())
                                .setCreateTime(article.getCreateTime())
                                .setUpdateTime(article.getUpdateTime());
                        return node;
                    })
                    .collect(Collectors.toList());
            columnVo.setDirContent(collect);
            return columnVo;
        }

        // 目录id不为空
        List dirNodeList = getColumnDirContent(authorId, userId, dirContentId, articleList, columnVo);
        columnVo.setDirContent(dirNodeList);
        return columnVo;
    }

    @Override
    public PageBean<FeaturedArticleVo> getRecommendedArticleList(BaseRequest<RecommendRequestDTO> baseRequest, Long userId) {
        RecommendRequestDTO data = baseRequest.getData();
        /**
         * 获取思路：1. 未登录用户直接获取排行榜数据，每次取20条，然后打乱顺序，并且保证第一二三不在前面
         *         2. 登录用户先从elasticsearch获取用户画像
         *              如果用户画像为空，则直接获取推荐系统
         *              如果推荐系统为空，则获取最新公开发布的文章
         *         3. 获取到用户画像，拿到category和tags，搜索elasticsearch中的文章列表
         *              如果没有搜索到文章，则直接获取推荐系统
         */
        if (userId == null) {
            return getRankPage(null, data);
        }
        // 登录用户
        try {
            GetRequest getRequest = new GetRequest(SysConstant.ES_INDEX_USER, String.valueOf(userId));
            GetResponse getResponse = highLevelClient.get(getRequest, RequestOptions.DEFAULT);
            UserEsEntity esUser = JSONUtil.toBean(getResponse.getSourceAsString(), UserEsEntity.class);
            if (esUser != null && (StringUtils.hasLength(esUser.getCategory()) || StringUtils.hasLength(esUser.getTags()))) {
                SearchRequest searchRequest = new SearchRequest(SysConstant.ES_INDEX_ARTICLE);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                // 设置字段分词匹配
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                if (StringUtils.hasLength(esUser.getCategory())) {
                    boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_CATEGORY, esUser.getCategory()).boost(0.8F));
                }
                if (StringUtils.hasLength(esUser.getTags())) {
                    boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_TAG, esUser.getTags()).boost(1.0F));
                }
                searchSourceBuilder.query(boolQuery);
                // 设置分页参数
                searchSourceBuilder.from(data.getOffset());
                searchSourceBuilder.size(data.getLimit());
                // 只获取id字段
                searchSourceBuilder.fetchSource(new String[]{"uid"}, null);
                searchRequest.source(searchSourceBuilder);
                // 发送请求并处理响应
                return searchEsArticleList(userId, searchRequest, data);
            }
            return getRankPage(userId, data);
        } catch (Exception e) {
            log.error("[获取推荐文章] --- 请求es数据错误，errorInfo: {}", e.toString());
            return getRankPage(userId, data);
        }
    }

    /**
     * @description: 获取排行榜分页数据
     * @param: Long
     * @return: PageBean<RecommendArticleVo>
     * @author: lovbe0210
     * @date: 2025/1/7 15:37
     */
    private PageBean<FeaturedArticleVo> getRankPage(Long userId, RecommendRequestDTO data) {
        PageBean<FeaturedArticleVo> rankArticleList = getRankArticleList(data, userId);
        // 如果获取的是排行榜，需要重新排序，最好不要将123放到最前面
        List<FeaturedArticleVo> list = rankArticleList.getList();
        if (list.size() > 0) {
            Collections.reverse(list);
            Collections.shuffle(list, new Random());
        }
        return rankArticleList;
    }

    @Override
    public void reportArticleView(Double st, Double csh, Double sh, String sign, Long userId) {
        // 参数合法性校验 st:scrollTop(div滚动条移动的位置) csh:canScrollHeight(div除过可视区域的部分) sh:scrollHeight(div总高度)
        // 1. 参数sign解析
        Long targetId = null;
        String uniqueId = null;
        try {
            String decodedStr = Base64.decodeStr(CommonUtils.bitwiseInvert(sign));
            JSONObject parseObj = JSONUtil.parseObj(decodedStr);
            uniqueId = parseObj.getStr(SysConstant.UNIQUE_ID);
            targetId = parseObj.getLong(SysConstant.TARGET_ID);
            Double pST = parseObj.getDouble(SysConstant.ST);
            Double pCSH = parseObj.getDouble(SysConstant.CSH);
            Double pSH = parseObj.getDouble(SysConstant.SH);
            if (!Objects.equals(st, pST) || !Objects.equals(csh, pCSH) || !Objects.equals(sh, pSH) ||
                    !StringUtils.hasLength(uniqueId) || targetId == null) {
                return;
            }
            // 2. 进度计算参数必须为csh不为0，且csh/sh < 0.1 或(csh/sh >= 0.1 && st/csh >= 0.1)
            if (csh == 0 || sh == 0) {
                return;
            }
            if ((csh / sh) > 0.1 && st / csh < 0.1) {
                return;
            }
        } catch (Exception e) {
            log.error("[阅读记录上报] --- sign解析异常，errorInfo: {}", e.toString());
        }

        BrowseHistoryDo historyDo = new BrowseHistoryDo();
        Date now = new Date();
        historyDo.setStatus(CommonStatusEnum.NORMAL.getStatus())
                .setCreateTime(now)
                .setUpdateTime(now);
        historyDo.setHistoryDate(now);
        // 3. 如果是登录用户直接用userId作为唯一id
        if (userId != null) {
            // 阅读记录入库，判断是更新还是新增
            historyDo.setUserId(userId)
                    .setTargetId(targetId)
                    .setTargetType(1)
                    .setUid(targetId + "_" + userId);
            int update = browseHistoryDao.atomicInsertOrUpdate(historyDo);
            if (update == 1) {
                // 新增时统计阅读量
                commonService.sendMessage(appName, browseActionTopic, historyDo);
            }
            return;
        }
        // 4. 如果未登录，先进行ip地址限制判断， 然后取uniqueId进行验证
        String ipLimit = RedisKeyConstant.getViewReportIpLimit(ServletUtils.getClientIP(), targetId);
        if (RedisUtil.hasKey(ipLimit)) {
            return;
        }
        RedisUtil.set(ipLimit, null, SysConstant.DAY_1);
        String reportLimitKey = RedisKeyConstant.getViewReportFrequencyLimit(uniqueId, targetId);
        if (RedisUtil.hasKey(reportLimitKey)) {
            return;
        }
        RedisUtil.set(reportLimitKey, null, SysConstant.DAY_1);
        historyDo.setTargetId(targetId)
                .setTargetType(1);
        commonService.sendMessage(appName, browseActionTopic, historyDo);
    }

    @Override
    public List<FeaturedArticleVo> getFeaturedArticle() {
        PageBean<FeaturedArticleVo> rankArticleList = getRankArticleList(new RecommendRequestDTO(3, 0), null);
        return rankArticleList.getList();
    }

    @Override
    public List<RecommendColumnVo> getFeaturedColumn() {
        PageBean<RecommendColumnVo> rankArticleList = getRankColumn(new RecommendRequestDTO(3, 0), null);
        return rankArticleList.getList();
    }

    @Override
    public PageBean<RecommendColumnVo> getRankColumn(RecommendRequestDTO data, Long userId) {
        String rankSetKey = RedisKeyConstant.getRankSetKey(SysConstant.TARGET_TYPE_COLUMN);
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = RedisUtil.zsGetSet(
                rankSetKey, data.getOffset(), data.getOffset() + data.getLimit() - 1, true);
        if (CollectionUtils.isEmpty(typedTuples)) {
            return new PageBean<>(false, List.of());
        }
        boolean hasMore = typedTuples.size() == data.getLimit();
        List<Object> columnIds = typedTuples.stream()
                .map(tuple -> tuple.getValue())
                .collect(Collectors.toList());
        List<RecommendColumnVo> columnList = publicContentDao.selectPublicColumnList(columnIds);
        if (CollectionUtils.isEmpty(columnList)) {
            return new PageBean<>(hasMore, List.of());
        }
        Set<Object> collectSet = new HashSet<>();
        if (userId != null) {
            List<CollectDo> collectList = collectDao.selectList(new LambdaQueryWrapper<CollectDo>()
                    .eq(CollectDo::getUserId, userId)
                    .eq(CollectDo::getTargetType, SysConstant.TARGET_TYPE_COLUMN)
                    .eq(CollectDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
            if (!CollectionUtils.isEmpty(collectList)) {
                collectSet.addAll(collectList.stream().map(CollectDo::getTargetId).collect(Collectors.toSet()));
            }
        }
        Map<Long, RecommendColumnVo> columnMap = columnList.stream()
                .peek(column -> {
                    // 如果是登录用户获取点赞状态和收藏状态
                    if (userId != null) {
                        column.setIfCollect(collectSet.contains(column.getUid()));
                    }
                })
                .collect(Collectors.toMap(RecommendColumnVo::getUid, Function.identity(), (a, b) -> b));
        columnList = columnIds.stream()
                .map(uid -> columnMap.get(uid))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new PageBean<>(hasMore, columnList);
    }

    @Override
    public PageBean<RecommendColumnVo> getPublicColumn(RecommendRequestDTO data, Long userId) {
        List<RecommendColumnVo> publicColumn = publicContentDao.selectPagePublicColumnList(data);
        return new PageBean<>(publicColumn != null && publicColumn.size() == data.getLimit(), publicColumn);
    }

    @Override
    public List<UserInfoDo> getExcellentAuthor() {
        PageBean<UserInfoDo> pageBean = getRankAuthor(new RecommendRequestDTO(3, 0), null);
        return pageBean.getList();
    }

    @Override
    public PageBean<UserInfoDo> getRankAuthor(RecommendRequestDTO data, Long userId) {
        String rankSetKey = RedisKeyConstant.getRankSetKey(SysConstant.TARGET_TYPE_AUTHOR);
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = RedisUtil.zsGetSet(
                rankSetKey, data.getOffset(), data.getOffset() + data.getLimit() - 1, true);
        if (CollectionUtils.isEmpty(typedTuples)) {
            return new PageBean<>(false, List.of());
        }
        boolean hasMore = typedTuples.size() == data.getLimit();
        Set<Object> followSet = new HashSet<>();
        if (userId != null) {
            // TODO
//            String userLikedSet = RedisKeyConstant.getUserLikesSet(userId);
//            likeSet.addAll(RedisUtil.zsGetSet(userLikedSet, 0, -1));
        }
        List<UserInfoDo> collect = typedTuples.stream()
                .map(tuple -> {
                    Long uid = (Long) tuple.getValue();
                    UserInfoDo userInfo = commonService.getCacheUser(uid);
                    if (userId != null) {
                        userInfo.setIsFollow(followSet.contains(uid) ? 1 : 0);
                    }
                    return userInfo;
                })
                .collect(Collectors.toList());
        return new PageBean<>(hasMore, collect);
    }

    @Override
    public PageBean<FeaturedArticleVo> getCategoryArticleList(BaseRequest<RecommendRequestDTO> baseRequest, Long userId) {
        RecommendRequestDTO requestData = baseRequest.getData();
        // 通过elasticsearch进行搜索文章id
        SearchRequest searchRequest = new SearchRequest(SysConstant.ES_INDEX_ARTICLE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 设置字段分词匹配
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 先按照一二级分类进行获取，然后再使用AI总结的分类进行获取
        String firstCategory = requestData.getFirstCategory();
        String secondCategory = requestData.getSecondCategory();
        if (secondCategory != null) {
            boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_CATEGORY2, secondCategory).boost(1.0F));
        } else {
            boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_CATEGORY1, firstCategory).boost(1.0F));
        }
        String firstCateMenu = "";
        String category = "";
        List<MenuDTO> menuList = publicContentDao.selecctMenuList();
        if (!CollectionUtils.isEmpty(menuList)) {
            for (MenuDTO menu : menuList) {
                if (Objects.equals(firstCategory, menu.getMenuCode())) {
                    firstCateMenu = menu.getMenuName();
                }
                // 查询全部时，使用一级分类和所有二级分类
                if (Objects.equals(menu.getMenuCode(), secondCategory) && secondCategory != null) {
                    category += menu.getMenuName();
                    break;
                }else if (Objects.equals(menu.getParentCode(), firstCategory) && secondCategory == null) {
                    if (category.length() != 0) {
                        category += ",";
                    }
                    category += menu.getMenuName();
                }

            }
        }
        if (secondCategory == null) {
            category = firstCateMenu + "," + category;
        }
        if (StringUtils.hasLength(category)) {
            boolQuery.should(QueryBuilders.matchQuery(SysConstant.ES_FILED_CATEGORY, category).boost(0.5F));
        }
        log.info("[es搜索] --- 查询语句：{}", boolQuery.toString());
        searchSourceBuilder.query(boolQuery);
        // 设置分页参数
        searchSourceBuilder.from(requestData.getOffset());
        searchSourceBuilder.size(requestData.getLimit());
        // 只获取id字段
        searchSourceBuilder.fetchSource(new String[]{"uid"}, null);
        searchRequest.source(searchSourceBuilder);
        // 发送请求并处理响应
        try {
            return searchEsArticleList(userId, searchRequest, requestData);
        } catch (IOException e) {
            log.error("[获取分类文章] --- 查询异常，errorInfo: {}", e.toString());
            return new PageBean<>(true, List.of());
        }
    }

    /**
     * @description: 获取排行榜文章
     * @param: RecommendRequestDTO
     * @return: PageBean<RecommendArticleVo>
     * @author: lovbe0210
     * @date: 2025/1/6 23:45
     */
    public PageBean<FeaturedArticleVo> getRankArticleList(RecommendRequestDTO data, Long userId) {
        String rankSetKey = RedisKeyConstant.getRankSetKey(SysConstant.TARGET_TYPE_ARTICLE);
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = RedisUtil.zsGetSet(
                rankSetKey, data.getOffset(), data.getOffset() + data.getLimit() - 1, true);
        if (CollectionUtils.isEmpty(typedTuples)) {
            return new PageBean<>(false, List.of());
        }
        boolean hasMore = typedTuples.size() == data.getLimit();
        List<Long> articleIds = typedTuples.stream()
                .map(tuple -> (Long) tuple.getValue())
                .collect(Collectors.toList());
        List<FeaturedArticleVo> articleList = publicContentDao.selectPublicArticleList(articleIds);
        if (CollectionUtils.isEmpty(articleList)) {
            return new PageBean<>(hasMore, List.of());
        }
        Set<Object> likeSet = new HashSet<>();
        if (userId != null) {
            String userLikedSet = RedisKeyConstant.getUserLikesSet(userId);
            likeSet.addAll(RedisUtil.zsGetSet(userLikedSet, 0, -1));
        }
        Map<Long, FeaturedArticleVo> articleMap = articleList.stream()
                .peek(article -> {
                    // 如果是登录用户获取点赞状态和收藏状态
                    if (userId != null) {
                        article.setIfLike(likeSet.contains(article.getUid()));
                    }
                })
                .collect(Collectors.toMap(FeaturedArticleVo::getUid, Function.identity(), (a, b) -> b));
        articleList = articleIds.stream()
                .map(uid -> articleMap.get(uid))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new PageBean<>(hasMore, articleList);
    }

    /**
     * @return List
     * @description 获取专栏目录内容
     * @param[1] dirContentId
     * @param[2] articleList
     * @param[3] columnVo
     * @author lovbe0210
     * @date 2024/11/23 23:55
     */
    private List getColumnDirContent(Long authorId, Long userId, Long dirContentId, List<ArticleDo> articleList, PublicColumnVo columnVo) {
        Map<Long, ArticleDo> articleMap = CollectionUtils.isEmpty(articleList) ? Collections.EMPTY_MAP : articleList.stream()
                .collect(Collectors.toMap(ArticleDo::getUid, Function.identity()));
        ContentDo contentDo = publicContentDao.selectContent(dirContentId);
        if (contentDo != null && StringUtils.hasLength(contentDo.getContent())) {
            String content = contentDo.getContent();
            try {
                JSONArray parseArray = JSONUtil.parseArray(content);
                if (CollectionUtils.isEmpty(parseArray) && CollectionUtils.isEmpty(articleList)) {
                    return Collections.EMPTY_LIST;
                }
                JSONArray finalParseArray = parseArray;
                if (CollectionUtils.isEmpty(parseArray) && !CollectionUtils.isEmpty(articleList)) {
                    articleList.stream()
                            .filter(articleDo -> Objects.equals(authorId, userId) ? true : articleDo.getPublishedContentId() != null)
                            .sorted((o1, o2) -> o1.getUpdateTime().compareTo(o2.getUpdateTime()))
                            .forEach(article -> {
                                DirNodeVo dirNode = new DirNodeVo()
                                        .setUid(article.getUid())
                                        .setUri(article.getUri())
                                        .setType(1)
                                        .setTitle(article.getTitle())
                                        .setSummary(article.getSummary())
                                        .setCreateTime(article.getCreateTime())
                                        .setUpdateTime(article.getUpdateTime());
                                finalParseArray.add(dirNode);
                            });
                    return finalParseArray;
                }
                Iterator<Object> iterator = parseArray.iterator();
                while (iterator.hasNext()) {
                    Object node = iterator.next();
                    checkNodeInfo(authorId, userId, (JSONObject) node, iterator, articleMap);
                }
                // 判断是否还有新建的文章没有添加到目录中去
                if (articleMap.size() > 0) {
                    articleMap.values().stream()
                            .sorted((o1, o2) -> o1.getUpdateTime().compareTo(o2.getUpdateTime()))
                            .peek(article -> {
                                DirNodeVo dirNode = new DirNodeVo()
                                        .setUid(article.getUid())
                                        .setType(1)
                                        .setTitle(article.getTitle())
                                        .setSummary(article.getSummary())
                                        .setCreateTime(article.getCreateTime())
                                        .setUpdateTime(article.getUpdateTime());
                                finalParseArray.add(0, dirNode);
                            })
                            .collect(Collectors.toList());
                }

                // 更新目录到数据库
                return parseArray;
            } catch (Exception e) {
                log.error("[获取专栏目录] --- 目录json解析异常，错误数据：{}", content);
            }
        }
        columnVo.setDirContent(Collections.EMPTY_LIST);
        return null;
    }

    /**
     * @description 检查专栏目录是否有需要删除或添加的文章
     * @param[1] authorId
     * @param[2] userId
     * @param[3] node
     * @param[4] iterator
     * @param[5] articleMap
     * @author lovbe0210
     * @date 2024/11/24 0:10
     */
    public void checkNodeInfo(Long authorId, Long userId, JSONObject node, Iterator<Object> iterator, Map<Long, ArticleDo> articleMap) {
        if (node.getInt("type") == 1) {
            // 文章节点
            Long uid = node.getLong("uid");
            ArticleDo articleDo = articleMap.get(uid);
            if (articleDo == null || (!Objects.equals(authorId, userId) && articleDo.getPublishedContentId() == null)) {
                // 文章已被删除或封禁
                iterator.remove();
            } else {
                node.set("title", articleDo.getTitle())
                        .set("summary", articleDo.getSummary())
                        .set("uri", articleDo.getUri())
                        .set("updateTime", articleDo.getUpdateTime());
                articleMap.remove(uid);
            }
        } else if (node.getInt("type") == 2) {
            node.set("expand", false);
            JSONArray children = node.getJSONArray("children");
            if (CollectionUtils.isEmpty(children)) {
                return;
            }
            Iterator<Object> childrenIterator = children.iterator();
            while (childrenIterator.hasNext()) {
                Object child = childrenIterator.next();
                checkNodeInfo(authorId, userId, (JSONObject) child, childrenIterator, articleMap);
            }
        }
    }

    /**
     * @description 查询es中文档id，然后通过数据库获取文章列表
     * @param[2] userId
     * @param[3] searchRequest
     * @param[4] requestData
     * @author lovbe0210
     * @date 2024/11/24 0:10
     */
    private PageBean<FeaturedArticleVo> searchEsArticleList(Long userId, SearchRequest searchRequest, RecommendRequestDTO requestData) throws IOException {
        SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        if (searchHits != null && searchHits.getTotalHits().value == 0) {
            // 搜索结果为空
            return new PageBean<>(false, List.of());
        }
        boolean hasMore = searchHits.getTotalHits().value == requestData.getLimit();
        List<Long> articleIds = Arrays.stream(searchHits.getHits())
                .map(hit -> Long.parseLong(hit.getId()))
                .collect(Collectors.toList());
        List<FeaturedArticleVo> articleList = publicContentDao.selectPublicArticleList(articleIds);
        if (CollectionUtils.isEmpty(articleList)) {
            return new PageBean<>(hasMore, List.of());
        }
        // 查询点赞记录
        String userLikedSet = RedisKeyConstant.getUserLikesSet(userId);
        Set<Object> likeSet = RedisUtil.zsGetSet(userLikedSet, 0, -1);
        Map<Long, FeaturedArticleVo> articleMap = articleList.stream()
                .peek(article -> {
                    String tagsStr = article.getTagsStr();
                    if (StringUtils.hasLength(tagsStr)) {
                        List<Map> tagList = JsonUtils.parseArray(tagsStr, Map.class);
                        article.setTags(tagList);
                    }
                })
                .collect(Collectors.toMap(FeaturedArticleVo::getUid, Function.identity()));
        List<FeaturedArticleVo> recommendArticles = articleIds.stream()
                .map(articleId -> articleMap.get(articleId))
                .filter(Objects::nonNull)
                .peek(articleVo -> articleVo.setIfLike(likeSet.contains(articleVo.getUid())))
                .collect(Collectors.toList());
        return new PageBean<>(hasMore, recommendArticles);
    }
}
