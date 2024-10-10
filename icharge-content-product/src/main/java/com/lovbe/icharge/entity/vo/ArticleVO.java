package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:56
 * @Description: MS
 */
@Accessors(chain = true)
@Data
public class ArticleVO extends BaseEntity {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 最新内容版本id
     */
    private String tmpContentId;
    /**
     * 已发布内容版本id
     */
    private String publishedContentId;
    /**
     * 所属用户id
     */
    private Long userId;
    /**
     * 所属专栏
     */
    private Long columnId;
    /**
     * 文章摘要
     */
    private String summary;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 文章标签
     */
    private String tagsArray;
    /**
     * 是否公开访问 0否1是
     */
    private int isPublic;
    /**
     * 发布状态 0未发布 1审核中 2审核失败 3已发布
     */
    private int publishStatus;
}
