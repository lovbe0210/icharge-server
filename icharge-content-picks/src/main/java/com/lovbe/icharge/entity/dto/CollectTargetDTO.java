package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/25 23:29
 * @Description: MS
 */
@Data
public class CollectTargetDTO {
    /**
     * 标记目标id
     */
    private Long targetId;
    /**
     * 标记目标类型 1文章 2专栏
     */
    @Max(message = "收藏类型错误", value = 2L)
    @Min(message = "收藏类型错误", value = 1L)
    private Integer targetType;
    /**
     * 收藏项id
     */
    private Long ftId;
    /**
     * 收藏夹分类标签id
     */
    private Set<Long> tags;
}
