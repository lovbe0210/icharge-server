package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.vo.RecommendColumnVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/15 17:11
 * @Description: 全局搜索结果
 */
@Data
@Accessors(chain = true)
public class SearchResultVo {
    /**
     * 搜索结果-用户
     */
    private List<SearchUserVo> searchUserList;
    /**
     * 搜索用户相关数
     */
    private int searchUserCount;
    /**
     * 搜索结果-专栏
     */
    private List<RecommendColumnVo> searchColumnList;
    /**
     * 搜索专栏相关数
     */
    private int searchColumnCount;
    /**
     * 搜索结果-文章
     */
    private List<FeaturedArticleVo> searchArticleList;
    /**
     * 搜索文章相关数
     */
    private int searchArticleCount;
}
