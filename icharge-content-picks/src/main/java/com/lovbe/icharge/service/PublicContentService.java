package com.lovbe.icharge.service;


import com.lovbe.icharge.entity.PublicArticleVo;

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
}
