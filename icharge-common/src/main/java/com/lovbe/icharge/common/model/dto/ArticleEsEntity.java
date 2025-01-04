package com.lovbe.icharge.common.model.dto;

import com.lovbe.icharge.common.annotation.EsFieldType;
import com.lovbe.icharge.common.annotation.EsIndex;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EsIndex("c_article")
public class ArticleEsEntity {
    /**
     * 主键 避免超出长度 前端丢失精度
     */
    @EsFieldType("long")
    private Long uid;
    /**
     * 文章标题
     */
    @EsFieldType("text")
    private String title;
    /**
     * 文章内容
     */
    @EsFieldType("text")
    private String content;
}
