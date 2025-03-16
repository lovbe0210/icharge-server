package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/20 15:50
 * @Description: MS
 */
@Data
public class RamblyJotDTO {
    /**
     * 随笔id
     */
    private Long uid;
    /**
     * 随笔内容
     */
    private Object content;
    /**
     * 文章摘要
     */
    @Length(max = 300, message = "随笔预览内容不得超过255字")
    private String previewContent;
    /**
     * 字数
     */
    private int wordsNum;
    /**
     * 预览图片
     */
    private List<String> previewImg;
    /**
     * 是否公开访问 0否1是
     */
    @Min(value = 0, message = "访问权限状态错误")
    @Max(value = 1, message = "访问权限状态错误")
    private Integer isPublic = 1;
}
