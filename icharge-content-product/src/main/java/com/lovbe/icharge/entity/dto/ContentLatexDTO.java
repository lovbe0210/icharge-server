package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/4/5 0:49
 * @Description: 文章内容数学公式
 */
@Data
public class ContentLatexDTO {
    @NotNull(message = "公式内容不得为空")
    private String content;
}
