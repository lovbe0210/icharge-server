package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/20 15:50
 * @Description: 创作记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("c_create_record")
public class CreateRecordDo extends BaseEntity {
    /**
     * 创作类型 创作类型1发布文章 2创建专栏 3发布随笔
     */
    private int targetType;
    /**
     * 用户id
     */
    private Long userId;
}
