package com.lovbe.icharge.common.model.dto;

import com.lovbe.icharge.common.annotation.EsFieldType;
import com.lovbe.icharge.common.annotation.EsIndex;
import com.lovbe.icharge.common.enums.SysConstant;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EsIndex(SysConstant.ES_INDEX_ARTICLE)
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
    /**
     * 系统推荐文章标签
     */
    @EsFieldType(value = "text", analyzer = "ik_smart")
    private String tags;
    /**
     * 系统推荐文章分类
     */
    @EsFieldType(value = "text", analyzer = "ik_smart")
    private String category;
    /**
     * 用户输入标签
     */
    @EsFieldType(value = "text", analyzer = "ik_smart")
    private String userTags;
    /**
     * 用户输入一级分类
     */
    @EsFieldType(value = "text", analyzer = "ik_smart")
    private String firstCategory;
    /**
     * 用户输入二级分类
     */
    @EsFieldType(value = "text", analyzer = "ik_smart")
    private String secondCategory;
}
