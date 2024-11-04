package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/20 15:50
 * @Description: MS
 */
@Data
public class ColumnDTO {
    @NotNull(message = "专栏id不得为空")
    private Long uid;
    /**
     * 文章标题
     */
    @Length(max = 30, message = "专栏标题不得超过30字")
    private String title;
    /**
     * 专栏简介
     */
    @Length(max = 255, message = "专栏简介不得超过255字")
    private String synopsis;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 封面图片
     */
    private MultipartFile coverFile;
    /**
     * 是否公开访问 0否1是
     */
    @Min(value = 0, message = "文档访问权限状态错误")
    @Max(value = 1, message = "文档访问权限状态错误")
    private Integer isPublic;
    /**
     * 是否开启评论功能 0否1是
     */
    @Min(value = 0, message = "评论功能状态错误")
    @Max(value = 1, message = "评论功能状态错误")
    private Integer enableComment;
    /**
     * 是否自动发布 0否1是 需要在公开访问时才能发布
     */
    @Min(value = 0, message = "自动发布状态错误")
    @Max(value = 1, message = "自动发布状态错误")
    private Integer autoPublish;
}
