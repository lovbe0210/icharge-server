package com.lovbe.icharge.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/20 15:28
 * @Description: 电池激励明细
 */
@Data
@Accessors(chain = true)
public class EncourageLogVo {
    private Long uid;
    /**
     * 行为类型
     */
    private int behaviorType;
    /**
     * 行为类型
     */
    private String behaviorMark;
    /**
     * 动作对象名称
     */
    private String targetName;
    /**
     * 激励分数
     */
    private int encourageScore;
    /**
     * 创建时间
     */
    private Date createTime;
}
