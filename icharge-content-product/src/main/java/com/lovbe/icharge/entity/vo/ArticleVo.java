package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:56
 * @Description: MS
 */
@Accessors(chain = true)
@Data
public class ArticleVo extends BaseEntity {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 展示路径
     */
    private String uri;
    /**
     * 最新内容版本id
     */
    private Long latestContentId;
    /**
     * 所属用户id
     */
    private Long userId;
    /**
     * 字数
     */
    private Integer wordsNum = 0;
    /**
     * 所属专栏uri
     */
    private String columnUri;
    /**
     * 专栏名称
     */
    private String columnName;
    /**
     * 文章摘要
     */
    private String summary;
    /**
     * 是否自动生成摘要
     */
    private Integer autoSummary = 1;
    /**
     * 是否置顶 当sort为空则不是置顶文章
     */
    private Integer sort;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 一级分类
     */
    private String firstCategory;
    /**
     * 二级分类
     */
    private String secondCategory;
    /**
     * 文章标签
     */
    private List<Map> tags = new ArrayList<>();
    /**
     * 是否公开访问 0否1是
     */
    private Integer isPublic;
    /**
     * 发布状态 0未发布 1审核中 2审核失败 3已发布
     */
    private Integer publishStatus;
    /**
     * 正文字体大小
     */
    private Integer bodyFontSize;
}
