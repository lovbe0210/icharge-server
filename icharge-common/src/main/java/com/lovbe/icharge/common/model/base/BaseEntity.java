package com.lovbe.icharge.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: 基类
 * @author: Lvhl
 * @date: 2024/5/10 18:54
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = false)
public class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 主键 避免超出长度 前端丢失精度
     */
    private Integer uid;

    /**
     * 数据状态
     */
    private String status;

    /**
     * 创建时间
     **/
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    /**
     * 更新时间
     **/
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;

}
