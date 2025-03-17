package com.lovbe.icharge.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/17 21:41
 * @Description: MS
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateDayLabelVo {
    /**
     * 日期
     */
    private String date;
    /**
     * 周
     */
    private String week;
    /**
     * 活跃等级
     */
    private int level;
}
