package com.lovbe.icharge.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/30 23:45
 * @Description: 内容发布DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContentPublishDTO {
    /**
     * 发布主体id
     */
    private Long targetId;
    /**
     * 1文章、2专栏首页、3随笔 4评论
     */
    private Integer targetType;
    /**
     * 内容id
     */
    private Long contentId;
    /**
     * 发布时间
     */
    private Date publishTime;
}
