package com.lovbe.icharge.service;


import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.entity.dto.RecommendRequestDTO;
import com.lovbe.icharge.entity.vo.PublicArticleVo;
import com.lovbe.icharge.entity.vo.PublicColumnVo;
import com.lovbe.icharge.entity.vo.RecommendArticleVo;
import com.lovbe.icharge.entity.vo.RouterInfoVo;

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
    List<RecommendArticleVo> getRecommendedArticleList(BaseRequest<RecommendRequestDTO> baseRequest, Long userId);

    /**
     * @description: 阅读进度上报
     * @param st
     * @param csh
     * @param sh
     * @param sign
     * @param userId
     */
    void reportArticleView(Double st, Double csh, Double sh, String sign, Long userId);
}
