package com.lovbe.icharge.entity;

import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/21 23:04
 * @Description: 路由信息
 */
@Data
public class RouterInfoVo {
    /**
     * 1文章 2专栏
     */
    private Integer type;
    /**
     * 用户id
     */
    private Long userId;
}
