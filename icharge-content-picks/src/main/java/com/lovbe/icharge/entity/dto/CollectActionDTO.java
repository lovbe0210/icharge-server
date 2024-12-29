package com.lovbe.icharge.entity.dto;

import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 13:46
 * @Description: MS
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CollectActionDTO extends BaseEntity {
    /**
     * 收藏对象类型  1文章 2专栏
     */
    private Integer targetType;
    /**
     * 收藏动作 1收藏 0取消
     */
    private Integer action;
}
