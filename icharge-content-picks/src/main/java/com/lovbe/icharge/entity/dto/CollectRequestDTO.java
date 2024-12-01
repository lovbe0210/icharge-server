package com.lovbe.icharge.entity.dto;

import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/30 21:57
 * @Description: 收藏请求实体类
 */
@Data
public class CollectRequestDTO {
    /**
     * 分组标签
     */
    private Long selectTag;
    /**
     * 搜索关键字
     */
    private String keywords;
    /**
     * 收藏类型 1文章 2专栏
     */
    private String collectType;
    /**
     * 名称排序 asc，desc, normal
     */
    private String titleSort;
    /**
     * 收藏时间排序 asc，desc, normal
     */
    private String timestampSort;
    /**
     * 查询坐标
     */
    private int offset;
    /**
     * 每页查询数量
     */
    private int limit = 100;
}
