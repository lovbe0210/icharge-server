package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/20 15:50
 * @Description: 电池激励记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("p_encorage_log")
public class EncourageLogDo extends BaseEntity {
    /**
     * 动作行为类型 1发布文章 2内容获得点赞 3文档获得精选 4内容获得评论 5新增粉丝
     */
    private int behaviorType;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 动作对象id，可以是文档id
     */
    private Long targetId;
    /**
     * 动作对象名称，暂时只做记录不做跳转
     */
    private String targetName;
    /**
     * 激励分数
     */
    private int encourageScore;
}
