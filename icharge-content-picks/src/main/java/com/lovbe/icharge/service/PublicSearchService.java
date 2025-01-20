package com.lovbe.icharge.service;


import com.lovbe.icharge.entity.dto.GlobalSearchDTO;
import com.lovbe.icharge.entity.vo.*;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 0:30
 * @Description: 公开内容搜索
 */
public interface PublicSearchService {
    /**
     * @description: 全局搜索
     * @param: data
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.PageBean<com.lovbe.icharge.entity.vo.SearchResultVo>
     * @author: lovbe0210
     * @date: 2025/1/15 17:27
     */
    SearchResultVo getGlobalSearchResult(GlobalSearchDTO data, Long userId);

    /**
     * @description:  范围搜索
     * @param: data
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.vo.FeaturedArticleVo>
     * @author: lovbe0210
     * @date: 2025/1/15 19:48
     */
    List<FeaturedArticleVo> getScopeSearchResult(GlobalSearchDTO data, Long userId);

    /**
     * @description: 全局搜索用户
     * @param: data
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.vo.ExcellentUserVo>
     * @author: lovbe0210
     * @date: 2025/1/19 16:17
     */
    List<ExcellentUserVo> getGlobalSearchUserList(GlobalSearchDTO data, Long userId);

    /**
     * @description: 全局搜索专栏
     * @param: data
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.vo.RecommendColumnVo>
     * @author: lovbe0210
     * @date: 2025/1/19 21:48
     */
    List<RecommendColumnVo> getGlobalSearchColumnList(GlobalSearchDTO data, Long userId);

    /**
     * @description: 全局搜索文章
     * @param: data
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.vo.FeaturedArticleVo>
     * @author: lovbe0210
     * @date: 2025/1/19 21:53
     */
    List<FeaturedArticleVo> getGlobalSearchArticleList(GlobalSearchDTO data, Long userId);
}
