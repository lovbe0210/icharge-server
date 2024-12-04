package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/5 0:27
 * @Description: 点赞相关DTO
 */
@Data
public class ContentLikeDTO {
    /**
     * 点赞对象id
     */
    @NotNull(message = "点赞对象不得为空")
    private Long targetId;
    /**
     * 点赞对象类型  1文章 2专栏 3随笔 4评论
     */
    @Min(value = 1, message = "点赞对象类型错误")
    @Max(value = 4, message = "点赞对象类型错误")
    @NotNull(message = "点赞对象类型不得为空")
    private Integer targetType;
}
