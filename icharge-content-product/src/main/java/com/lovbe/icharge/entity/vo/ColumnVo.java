package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import lombok.Data;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/29 22:36
 * @Description: 专栏
 */
@Data
public class ColumnVo extends BaseEntity {
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
     * 目录数据id
     */
    private Long dirContentId;
    /**
     * 是否公开访问 0否1是
     */
    private Integer isPublic;
    /**
     * 是否开启评论功能 0否1是
     */
    private Integer enableComment;
    /**
     * 是否自动发布 0否1是 需要在公开访问时才能发布
     */
    private Integer autoPublish;
    /**
     * 文章列表
     */
    private List<ArticleDo> articleList;
}
