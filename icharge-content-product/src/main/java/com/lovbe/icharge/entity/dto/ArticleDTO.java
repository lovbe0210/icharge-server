package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/20 15:50
 * @Description: MS
 */
@Data
public class ArticleDTO {
    @NotNull(message = "文档id不得为空")
    private Long uid;
    /**
     * 文章标题
     */
    @Length(max = 50, message = "文档标题不得超过50字")
    private String title;
    /**
     * 最新内容版本id
     */
    private String latestContentId;
    /**
     * 字数
     */
    private Integer wordsNum;
    /**
     * 所属专栏
     */
    private Long columnId;
    /**
     * 文章摘要
     */
    @Length(max = 150, message = "文档摘要不得超过150字")
    private String summary;
    /**
     * 自动生成摘要 0否1是
     */
    private Integer autoSummary;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 封面图片
     */
    private MultipartFile coverFile;
    /**
     * 文章标签 单个标签10字符
     */
    @Length(max = 200, message = "标签格式错误")
    private String tagsArray;
    /**
     * 是否公开访问 0否1是
     */
    @Min(value = 0, message = "访问权限状态错误")
    @Max(value = 1, message = "访问权限状态错误")
    private Integer isPublic;
    /**
     * 一级分类
     */
    private String firstCategory;
    /**
     * 二级分类
     */
    private String secondCategory;
}
