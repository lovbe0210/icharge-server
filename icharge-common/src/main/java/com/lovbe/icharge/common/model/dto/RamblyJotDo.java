package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/15 13:06
 * @Description: 随笔
 */
@TableName(value = "c_essay", autoResultMap = true)
@Data
@Accessors(chain = true)
public class RamblyJotDo extends BaseEntity {
    /**
     * 标题
     */
    private String title;
    /**
     * 所属用户id
     */
    private Long userId;
    /**
     * 内容id
     */
    private Long contentId;
    /**
     * 预览内容
     */
    private String previewContent;
    /**
     * 预览图片
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> previewImg = new ArrayList<>();
    @TableField(exist = false)
    private String previewImgStr;
    /**
     * 点赞数
     */
    @TableField(exist = false)
    private Integer likeCount;
    /**
     * 评论数
     */
    @TableField(exist = false)
    private Integer commentCount;
    /**
     * 是否公开可见 0否1是
     */
    private Integer isPublic;
    /**
     * 发布状态 1审核中 2审核失败 3发布成功
     */
    private Integer publishStatus;
}