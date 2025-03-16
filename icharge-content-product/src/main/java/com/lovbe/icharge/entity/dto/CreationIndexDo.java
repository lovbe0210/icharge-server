package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/15 11:12
 * @Description: 创作指数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("p_creation_index")
public class CreationIndexDo extends BaseEntity {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 创作分数 不活跃-活跃 0-5级
     */
    private int creationScore;
    /**
     * 日期
     */
    private Date recordDate;
}
