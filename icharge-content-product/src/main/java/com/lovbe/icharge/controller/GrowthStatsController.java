package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.CreationIndexDo;
import com.lovbe.icharge.entity.dto.GrowthStatsDo;
import com.lovbe.icharge.service.GrowthStatsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/16 23:22
 * @Description: 成长轨迹
 */
@RestController
public class GrowthStatsController {
    @Resource
    private GrowthStatsService growthStatsService;

    /**
     * description: 获取成长轨迹
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ColumnVo>
     */
    @GetMapping("/growth/stats/{rangeType}")
    public ResponseBean<GrowthStatsDo> getGrowthStatList(@PathVariable("rangeType") Integer rangeType, @RequestHeader(SysConstant.USERID) long userId) {
        GrowthStatsDo growthStat = growthStatsService.getGrowthStatList(userId, rangeType);
        return ResponseBean.ok(growthStat);
    }

    /**
     * description: 获取创作指数
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ColumnVo>
     */
    @GetMapping("/creation/index")
    public ResponseBean<List<CreationIndexDo>> getCreationIndexList(@RequestHeader(SysConstant.USERID) long userId) {
        List<CreationIndexDo> creationIndexList = growthStatsService.getCreationIndexList(userId);
        return ResponseBean.ok(creationIndexList);
    }



}
