package com.lovbe.icharge.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/27 11:37
 * @Description: 推荐内容请求体
 */
@Data
@NoArgsConstructor
public class RecommendRequestDTO {
    /**
     * 用户id
     */
    private Long userId;
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
    /**
     * 排序方式 value: 0 最多浏览 1 最多收藏 2 最近更新
     */
    private String sortType;

    public int getLimit() {
        return limit < 1 ? 20 : limit > 100 ? 20 : limit;
    }

    public int getOffset() {
        return offset < 0 ? 0 : offset;
    }

    public RecommendRequestDTO(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }
}
