package com.lovbe.icharge.service;


import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.entity.dto.GlobalSearchDTO;
import com.lovbe.icharge.entity.dto.RecommendRequestDTO;
import com.lovbe.icharge.entity.vo.*;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 0:30
 * @Description: MS
 */
public interface PublicContentService {

    /**
     * @description 获取文档信息
     * @param[1] articleId
     * @param[2] userId
     * @return ArticleVo
     * @author lovbe0210
     * @date 2024/11/19 0:31
     */
    PublicArticleVo getArticleInfo(String uri, Long userId);

    /**
     * @description 根据uri判断为1文章还是2专栏
     * @param[1] dynamicId
     * @return Integer
     * @author lovbe0210
     * @date 2024/11/18 0:53
     */
    RouterInfoVo getRouterDirection(String dynamicId);

    /**
     * 获取文章列表
     * @param authorId
     * @param userId
     * @return
     */
    List<PublicArticleVo> getArticleList(Long authorId, Long userId);

    /**
     * @description 获取专栏目录
     * @param[1] uri
     * @param[2] userId
     * @return JSONArray
     * @author lovbe0210
     * @date 2024/11/23 22:21
     */
    PublicColumnVo getColumnDir(String uri, Long userId);

    /**
     * @description: 获取推荐文章列表
     * @param: BaseRequest<RecommendRequestDTO>
     * @return: List<RecommendArticleVo>
     * @author: lovbe0210
     * @date: 2024/12/27 13:23
     */
    PageBean<FeaturedArticleVo> getRecommendedArticleList(BaseRequest<RecommendRequestDTO> baseRequest, Long userId);

    /**
     * @description: 获取排行榜文章列表
     * @param: RecommendRequestDTO
     * @return: PageBean<RecommendArticleVo>
     * @author: lovbe0210
     * @date: 2025/1/7 15:44
     */
    PageBean<FeaturedArticleVo> getRankArticleList(RecommendRequestDTO data, Long userId);

    /**
     * @description: 阅读进度上报
     * @param st
     * @param csh
     * @param sh
     * @param sign
     * @param userId
     */
    void reportArticleView(Double st, Double csh, Double sh, String sign, Long userId);

    /**
     * @description: 获取精选文章
     * @param:
     * @return: List<RecommendArticleVo>
     * @author: lovbe0210
     * @date: 2025/1/7 14:03
     */
    List<FeaturedArticleVo> getFeaturedArticle();

    /**
     * @description: 获取精选专栏
     * @param:
     * @return: List<RecommendArticleVo>
     * @author: lovbe0210
     * @date: 2025/1/7 14:04
     */
    List<RecommendColumnVo> getFeaturedColumn();

    /**
     * @description: 获取专栏排行榜
     * @param: RecommendRequestDTO
     * @return: PageBean<RecommendColumnVo>
     * @author: lovbe0210
     * @date: 2025/1/9 16:50
     */
    PageBean<RecommendColumnVo> getRankColumn(RecommendRequestDTO requestDTO, Long userId);

    /**
     * @description: 获取公开专栏
     * @param: RecommendRequestDTO
     * @return: PageBean<RecommendColumnVo>
     * @author: lovbe0210
     * @date: 2025/1/10 17:14
     */
    PageBean<RecommendColumnVo> getPublicColumn(RecommendRequestDTO data, Long userId);

    /**
     * @description: 获取首页优秀创作者(排行榜前3）
     * @param:
     * @return: List<UserInfoDo>
     * @author: lovbe0210
     * @date: 2025/1/11 19:01
     */
    List<UserInfoDo> getExcellentAuthor();

    /**
     * @description: 获取创作者排行榜
     * @param: RecommendRequestDTO
     * @return: PageBean<UserInfoDo>
     * @author: lovbe0210
     * @date: 2025/1/11 19:02
     */
    PageBean<UserInfoDo> getRankAuthor(RecommendRequestDTO data, Long userId);

    /**
     * @description: 获取文章分类列表
     * @param: BaseRequest<RecommendRequestDTO>
     * @return: PageBean<FeaturedArticleVo>
     * @author: lovbe0210
     * @date: 2025/1/12 17:18
     */
    PageBean<FeaturedArticleVo> getCategoryArticleList(BaseRequest<RecommendRequestDTO> baseRequest, Long userId);

    /**
     * @description: 全局搜索
     * @param: data
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.PageBean<com.lovbe.icharge.entity.vo.SearchResultVo>
     * @author: lovbe0210
     * @date: 2025/1/15 17:27
     */
    ResponseBean<SearchResultVo> getGlobalSearchResult(GlobalSearchDTO data, Long userId);

    /**
     * @description:  范围搜索
     * @param: data
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.vo.FeaturedArticleVo>
     * @author: lovbe0210
     * @date: 2025/1/15 19:48
     */
    List<FeaturedArticleVo> getScopeSearchResult(GlobalSearchDTO data, Long userId);
}
