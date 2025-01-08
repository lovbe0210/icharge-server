package com.lovbe.icharge.service;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/29 11:30
 * @Description: MS
 */
public interface RecommendPicksService {
    /**
     * @description: 文章排行榜更新
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 11:33
     */
    void articleRankUpdate();

    /**
     * @description: 专栏排行榜更新
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 11:33
     */
    void columnRankUpdate();
    /**
     * @description: 人物画像标签提取
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/30 20:09
     */
    void portraitTagExtraction();

    /**
     * @description: 专栏信息统计
     * @param:
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/30 20:09
     */
    void columnStatisticUpdate();
}
