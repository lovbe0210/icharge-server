package com.lovbe.icharge.entity.dto;

import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/27 11:37
 * @Description: 推荐内容请求体
 */
@Data
public class RecommendRequestDTO {
    /**
     * 一级分类
     */
    private String firstCategory;
    /**
     * 二级分类
     */
    private String secondCategory;
    /**
     * 单次请求数据
     */
    private int limit = 20;
    /**
     * 请求坐标
     */
    private int offset = 0;

    public int getLimit() {
        return limit < 1 ? 20 : limit > 100 ? 20 : limit;
    }

    public int getOffset() {
        return offset < 0 ? 0 : offset;
    }
}
