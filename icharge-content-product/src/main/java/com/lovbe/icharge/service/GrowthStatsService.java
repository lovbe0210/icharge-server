package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.CreationIndexDo;
import com.lovbe.icharge.entity.dto.GrowthStatsDo;

import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/15 11:44
 * @Description: 创作统计
 */
public interface GrowthStatsService {
    /**
     * @description: 创作统计
     * @author: lovbe0210
     * @date: 2025/3/15 11:04
     */
    void creationStatistic();

    /**
     * @description: 创作指数
     * @author: lovbe0210
     * @date: 2025/3/15 11:05
     */
    void creationIndex();

    /**
     * @description: 获取成长轨迹统计数据
     * @param: userId
     * @param: rangeType
     * @return: java.util.List<com.lovbe.icharge.entity.dto.GrowthStatsDo>
     * @author: lovbe0210
     * @date: 2025/3/16 23:28
     */
    GrowthStatsDo getGrowthStatList(long userId, Integer rangeType);

    /**
     * @description: 获取创作指数
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.dto.CreationIndexDo>
     * @author: lovbe0210
     * @date: 2025/3/16 23:37
     */
    Map<String, List> getCreationIndexList(String domain);
}
