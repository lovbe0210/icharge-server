package com.lovbe.icharge.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @description: 基类
 * @author: Lvhl
 * @date: 2024/5/10 18:54
 */
@Data
@Accessors(chain = true)
public class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 主键 避免超出长度 前端丢失精度
     */
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    /**
     * 数据状态
     */
    private String status;

    /**
     * 创建时间
     **/
    private Date createTime;

    /**
     * 更新时间
     **/
    private Date updateTime;

}
