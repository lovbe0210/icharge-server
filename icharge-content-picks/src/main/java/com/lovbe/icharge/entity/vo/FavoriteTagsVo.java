package com.lovbe.icharge.entity.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/26 0:04
 * @Description: MS
 */
@Data
public class FavoriteTagsVo {
    @NotBlank(message = "分类标题不得为空")
    @Length(message = "分类标题不得超过30字")
    private String title;
}
