package com.lovbe.icharge.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/17 21:35
 * @Description: MS
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StateMonthLabelVo {
    /**
     * 月份
     */
    private String month;
    /**
     * 当月第一天在所有周的index
     */
    private int firstDayByWeek;
}
