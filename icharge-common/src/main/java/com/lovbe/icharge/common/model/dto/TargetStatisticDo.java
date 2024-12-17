package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/18 0:35
 * @Description: 社交统计
 */
@Data
@Accessors(chain = true)
public class TargetStatisticDo extends BaseEntity {
    /**
     * 类型 1文章 2专栏 3随笔
     */
     private String type;
    /**
     * 点赞数
     */
    private int likeCount;
    /**
     * 评论数
     */
    private int commentCount;
    /**
     * 收藏数
     */
    private int collectCount;
    /**
     * 浏览量
     */
    private int viewCount;
}
