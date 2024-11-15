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
public class CreateColumnDTO {
    /**
     * 文章标题
     */
    @NotBlank(message = "专栏名称不得为空")
    @Length(max = 30, message = "专栏名称不得超过30字")
    private String title;
    /**
     * 专栏简介
     */
    @Length(max = 150, message = "专栏简介不得超过255字")
    private String synopsis;
    /**
     * 是否公开访问 0否1是
     */
    @Min(value = 0, message = "访问权限状态错误")
    @Max(value = 1, message = "访问权限状态错误")
    private int isPublic;
}
