package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.CreationIndexDo;
import com.lovbe.icharge.entity.dto.GrowthStatsDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


/**
 * 用户相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface GrowthStatsDao extends BaseMapper<GrowthStatsDo> {
    /**
     * @description: 获取创作天数
     * @param: userIds
     * @return: java.util.List<com.lovbe.icharge.entity.dto.GrowthStatsDo>
     * @author: lovbe0210
     * @date: 2025/3/16 13:58
     */
    List<GrowthStatsDo> selectCreationCount(@Param("userIds") Set<Long> userIds);
}
