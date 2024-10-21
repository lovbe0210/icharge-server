package com.lovbe.icharge.common.model.dto;

import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/21 21:54
 * @Description: MS
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
}
