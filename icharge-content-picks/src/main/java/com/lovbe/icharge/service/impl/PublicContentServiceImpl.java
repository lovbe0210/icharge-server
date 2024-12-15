package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.UserInfo;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.ColumnDo;
import com.lovbe.icharge.common.model.dto.ContentDo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.vo.DirNodeVo;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.dao.PublicContentDao;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import com.lovbe.icharge.entity.vo.PublicArticleVo;
import com.lovbe.icharge.entity.vo.PublicColumnVo;
import com.lovbe.icharge.entity.vo.RouterInfoVo;
import com.lovbe.icharge.service.PublicContentService;
import com.lovbe.icharge.service.feign.SocialService;
import com.lovbe.icharge.service.feign.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
public class PublicContentServiceImpl implements PublicContentService {
    @Resource
    private PublicContentDao publicContentDao;
    @Resource
    private UserService userService;

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
}
