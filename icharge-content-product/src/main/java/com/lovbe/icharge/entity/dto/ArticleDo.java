package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TableName(value = "c_article", autoResultMap = true)
@Data
@Accessors(chain = true)
public class ArticleDo extends BaseEntity {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 最新内容版本id
     */
    private Long latestContentId;
    /**
     * 已发布内容版本id
     */
    private Long publishedContentId;
    /**
     * 所属用户id
     */
    private Long userId;
    /**
     * 所属专栏
     */
    private Long columnId;
    /**
     * 字数
     */
    private Integer wordsNum;
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
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Map> tags = new ArrayList<>();
    /**
     * 是否公开访问 0否1是
     */
    private Integer isPublic;
    /**
     * 发布状态 0未发布 1审核中 2审核失败 3已发布
     */
    private Integer publishStatus;
}
