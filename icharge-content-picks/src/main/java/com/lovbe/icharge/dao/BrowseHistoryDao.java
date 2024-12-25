package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.entity.dto.BrowseHistoryDo;
import com.lovbe.icharge.entity.dto.BrowseHistoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/25 23:48
 * @Description: MS
 */
@Mapper
public interface BrowseHistoryDao extends BaseMapper<BrowseHistoryDo> {
    /**
     * @description: 获取历史记录
     * @param: RequestListDTO
     * @return: List<BrowseHistoryVo>
     * @author: lovbe0210
     * @date: 2024/12/25 22:56
     */
    List<BrowseHistoryDTO> selectHistoryList(@Param("data") RequestListDTO data, @Param("userId") Long userId);

    void updateViewStatistic(@Param("statisticList") List<TargetStatisticDo> statisticList);
}
