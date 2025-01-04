package com.lovbe.icharge.common.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArticleEsEntity {
    /**
     * 主键 避免超出长度 前端丢失精度
     */
    private Long uid;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章内容
     */
    private String content;
}
