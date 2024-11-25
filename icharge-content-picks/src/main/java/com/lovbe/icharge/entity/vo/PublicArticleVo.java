package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 1:32
 * @Description: 公开文章信息
 */
@Accessors(chain = true)
@Data
public class PublicArticleVo extends BaseEntity {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 展示路径
     */
    private String uri;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 所属用户id
     */
    private Long userId;
    /**
     * 字数
     */
    private int wordsNum;
    /**
     * 所属专栏
     */
    private Long columnId;
    /**
     * 专栏名称
     */
    private String columnName;
    /**
     * 文章摘要
     */
    private String summary;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 文章标签
     */
    private List<Map> tags = new ArrayList<>();
    /**
     * 是否公开访问 0否1是
     */
    private Integer isPublic;
}
