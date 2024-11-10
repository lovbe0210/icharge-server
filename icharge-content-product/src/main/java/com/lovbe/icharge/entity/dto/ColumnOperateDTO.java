package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/9 1:15
 * @Description: 专栏内容批量操作
 */
@Data
public class ColumnOperateDTO {
    /**
     * 专栏id
     */
    @NotNull(message = "专栏id不得为空")
    private Long columnId;

    /**
     * 操作类型 1发布操作，2移出专栏，3批量导出，4批量删除
     */
    private String operateType;

    /**
     * 文章列表
     */
    @NotEmpty(message = "操作文章不得为空")
    private List<Long> articleList;
}
