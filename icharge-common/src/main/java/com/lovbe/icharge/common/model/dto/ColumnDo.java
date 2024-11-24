package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 专栏
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/10/11 22:19
 */
@TableName(value = "c_column")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ColumnDo extends BaseEntity {
    /**
     * 专栏标题
     */
    private String title;
    /**
     * 展示路径
     */
    private String uri;
    /**
     * 所属用户id
     */
    private Long userId;
    /**
     * 专栏简介
     */
    private String synopsis;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 目录内容id
     */
    private Long dirContentId;
    /**
     * 是否公开访问 0否1是
     */
    private int isPublic;
    /**
     * 是否开启评论功能 0否1是
     */
    private int enableComment;
    /**
     * 是否自动发布 0否1是 需要在公开访问时才能发布
     */
    private int autoPublish;
    /**
     * 文章列表
     */
    @TableField(exist = false)
    private List<ArticleDo> articleList;
}
