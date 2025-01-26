package com.lovbe.icharge.common.model.dto;

import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/21 21:54
 * @Description: 获取分页、搜索、排序的列表参数
 */
@Data
public class RequestListDTO {
    /**
     * 关键字
     */
    private String keywords;
    /**
     * 排序类型 1更新时间 2创建时间
     */
    private Integer sort;
    /**
     * 分页查询
     */
    private int offset = 0;
    private int limit = 20;

    public int getOffset() {
        return offset < 0 ? 0 : offset;
    }

    public int getLimit() {
        return limit <= 0 ? 20 : limit > 200 ? 200 : limit;
    }
}
