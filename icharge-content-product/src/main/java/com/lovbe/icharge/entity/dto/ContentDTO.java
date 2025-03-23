package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/20 16:09
 * @Description: 文档内容
 */
@Data
public class ContentDTO {
    /**
     * 内容id
     */
    private Long uid;
    /**
     * 文档内容
     */
    private Object content;
    /**
     * 字数
     */
    private int wordsNum;
    /**
     * 摘要
     */
    private String summary;
    /**
     * 封面
     */
    private String coverUrl;
    /**
     * 文档或专栏id
     */
    @NotNull(message = "文档或专栏id不得为空")
    private Long targetId;
}
