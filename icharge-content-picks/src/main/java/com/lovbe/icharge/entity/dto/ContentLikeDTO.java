package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/5 0:27
 * @Description: 点赞相关DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentLikeDTO {
    /**
     * 点赞对象id
     */
    private Long targetId;
    /**
     * 点赞对象类型  1文章 2专栏 3随笔 4评论
     */
    private Integer targetType;
}
