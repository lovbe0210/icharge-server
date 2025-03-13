package com.lovbe.icharge.common.model.dto;

import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/18 0:35
 * @Description: 社交统计
 */
@Data
@Accessors(chain = true)
public class TargetStatisticDo extends BaseEntity {
    /**
     * 用户等级
     */
    private int userLevel;
    /**
     * 类型 1文章 2专栏 3随笔 4评论
     * 0 用户
     */
     private Integer type;
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
    /**
     * 文章数
     */
    private int articleCount;
    /**
     * 专栏数
     */
    private int columnCount;
    /**
     * 用户粉丝数
     */
    private int fansCount;
    /**
     * 用户关注数
     */
    private int followCount;
    /**
     * 首次发布时间
     */
    private Date publishTime;
}
