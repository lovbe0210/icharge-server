package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 16:06
 * @Description: MS
 */
@Data
public class TargetCommentDTO {
    /**
     * 评论对象id
     */
    @NotNull(message = "评论对象不得为空")
    private Long targetId;
    /**
     * 查询坐标
     */
    private int offset;
    /**
     * 每页查询数量
     */
    private int limit = 100;

    public int getOffset() {
        return offset < 0 ? 0 : offset;
    }

    public int getLimit() {
        return limit <= 0 ? 100 : limit > 1000 ? 1000 : limit;
    }
}
