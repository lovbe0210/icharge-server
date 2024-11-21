package com.lovbe.icharge.service;


import com.lovbe.icharge.entity.PublicArticleVo;
import com.lovbe.icharge.entity.RouterInfoVo;

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
    PublicArticleVo getArticleInfo(String articleId, String userId);

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
     * @param domain
     * @param userId
     * @return
     */
    Object getArticleList(String domain, String userId);
}
