package com.lovbe.icharge.entity.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/20 16:09
 * @Description: 文档内容
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContentVO {
    /**
     * 内容id
     */
    private Long uid;
    /**
     * 文档内容
     */
    private Object content;
    /**
     * 文档所属用户id
     */
    @NotNull(message = "文档id不得为空")
    private Long articleId;
}
