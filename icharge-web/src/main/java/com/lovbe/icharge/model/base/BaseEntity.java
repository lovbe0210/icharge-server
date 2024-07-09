package com.lovbe.icharge.model.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * @description: 基类
 * @author: Lvhl
 * @date: 2024/5/10 18:54
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = false)
public class BaseEntity<T> {

    private static final long serialVersionUID = 1L;


    /**
     * 主键 避免超出长度 前端丢失精度
     */
    private String id;

    /**
     * 创建时间
     **/
//    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    /**
     * 更新时间
     **/
//    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;

}
