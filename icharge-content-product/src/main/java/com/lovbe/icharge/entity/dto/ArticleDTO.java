package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Map;

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
    @NotBlank(message = "文档标题不得为空")
    @Length(max = 30, message = "文档标题不得超过30字")
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
     * 封面地址
     */
    private String coverUrl;
    /**
     * 文章标签 单个标签10字符
     */
    @Length(max = 200, message = "标签格式错误")
    private String tagsArray;
    /**
     * 是否公开访问 0否1是
     */
    @Digits(integer = 1, fraction = 0, message = "文档访问权限状态错误")
    private int isPublic;
}
