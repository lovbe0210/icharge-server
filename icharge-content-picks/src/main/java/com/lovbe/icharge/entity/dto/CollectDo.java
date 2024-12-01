package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/25 23:49
 * @Description: 收藏夹实体类
 */
@Data
@Accessors(chain = true)
@TableName(value = "p_collect_item", autoResultMap = true)
public class CollectDo extends BaseEntity {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 目标id（文章或专栏id）
     */
    private Long targetId;
    /**
     * 收藏类型 1文章 2专栏
     */
    private Integer targetType;
    /**
     * 收藏标签分类
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<Long> tags;
}
