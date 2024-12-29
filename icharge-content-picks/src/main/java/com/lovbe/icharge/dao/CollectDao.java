package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.entity.dto.CollectDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/25 23:48
 * @Description: MS
 */
@Mapper
public interface CollectDao extends BaseMapper<CollectDo> {
    /** 
     * @description: 更新收藏统计
     * @param: List<TargetStatisticDo>
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 17:19
     */
    void updateCollectStatistic(@Param("statisticList") List<TargetStatisticDo> statisticList);
}
