package com.lovbe.icharge.entity.dto;

import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/5 17:42
 * @Description: MS
 */
@Data
public class DomainContentUpdateDTO {
    /**
     * 个人主页内容id
     */
    private Long contentId;
    /**
     * 个人主页内容
     */
    private Object content;
}
