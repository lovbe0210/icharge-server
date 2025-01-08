package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import lombok.Data;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/29 22:36
 * @Description: 推荐专栏信息
 */
@Data
public class RecommendColumnVo extends BaseEntity {
    /**
     * 专栏标题
     */
    private String title;
    /**
     * 展示路径
     */
    private String uri;
    /**
     * 专栏简介
     */
    private String synopsis;
    /**
     * 所属用户id
     */
    private Long userId;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 文章数
     */
    private int articleCount;
    /**
     * 订阅数
     */
    private int subCount;
    /**
     * 浏览数
     */
    private int viewCount;
}
