package com.lovbe.icharge.entity.dto;

import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/24 15:16
 * @Description: 社交通知请求类
 */
@Data
public class SocialNoticeReqDTO {
    /**
     * 单次请求数据
     */
    private int limit = 20;
    /**
     * 请求坐标
     */
    private int offset = 0;
    /**
     * 阅读状态 0否1是 null全部
     */
    private Integer readStatus;

    public int getLimit() {
        return limit < 1 ? 20 : limit > 100 ? 20 : limit;
    }

    public int getOffset() {
        return offset < 0 ? 0 : offset;
    }
}
