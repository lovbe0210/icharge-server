package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.entity.dto.BrowseHistoryDo;
import com.lovbe.icharge.entity.dto.BrowseHistoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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

    /**
     * @description: 浏览统计更新
     * @param: List<TargetStatisticDo>
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/26 22:38
     */
    void updateViewStatistic(@Param("statisticList") List<TargetStatisticDo> statisticList);

    /**
     * @description: 插入或保存原子操作
     * @param: BrowseHistoryDo
     * @return: int
     * @author: lovbe0210
     * @date: 2024/12/30 12:17
     */
    int atomicInsertOrUpdate(@Param("history") BrowseHistoryDo historyDo);

    /**
     * @param lastTime
     * @param targetType
     * @description: 从历史记录中获取有历史记录的用户id
     * @param:
     * @return: List<Long>
     * @author: lovbe0210
     * @date: 2025/1/5 15:48
     */
    List<Long> selectUserListFromHistory(@Param("lastTime") Date lastTime, @Param("targetType") Integer targetType);
}
