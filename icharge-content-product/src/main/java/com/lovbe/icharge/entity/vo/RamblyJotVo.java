package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:56
 * @Description: 随笔
 */
@Accessors(chain = true)
@Data
public class RamblyJotVo extends BaseEntity {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 最新内容版本id
     */
    private String content;
    /**
     * 预览内容
     */
    private String previewContent;
    /**
     * 所属用户id
     */
    private Long userId;
    /**
     * 封面地址
     */
    private List<String> previewImg;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 评论数
     */
    private Integer commentCount;
    /**
     * 是否公开访问 0否1是
     */
    private int isPublic;
    /**
     * 发布状态 1审核中 2审核失败 3已发布
     */
    private int publishStatus;
}
