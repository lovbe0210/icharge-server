package com.lovbe.icharge.common.model.dto;

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
     * 展示路径
     */
    private String uri;
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
     * 个人主页
     */
    @TableField(exist = false)
    private String domain;
    /**
     * 用户名
     */
    @TableField(exist = false)
    private String username;
    /**
     * 所属专栏
     */
    private Long columnId;
    /**
     * 专栏地址
     */
    @TableField(exist = false)
    private String columnUri;
    /**
     * 专栏名称
     */
    @TableField(exist = false)
    private String columnName;
    /**
     * 收藏id标识
     */
    @TableField(exist = false)
    private Long collectId;
    /**
     * 点赞用户列表
     */
    @TableField(exist = false)
    private List<UserInfoDo> likeUserList;
    /**
     * 字数
     */
    private Integer wordsNum;
    /**
     * 文章摘要
     */
    private String summary;
    /**
     * 是否自动生成摘要 0否1是
     */
    private int autoSummary = 1;
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
     * 排序字段，用于判断是否置顶
     */
    private Integer sort;
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
    /**
     * 点赞数
     */
    @TableField(exist = false)
    private int likeCount;
    /**
     * 评论数
     */
    @TableField(exist = false)
    private int commentCount;
    /**
     * 收藏数
     */
    @TableField(exist = false)
    private int collectCount;
    /**
     * 浏览量
     */
    @TableField(exist = false)
    private int viewCount;
}
