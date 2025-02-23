package com.lovbe.icharge.entity.dto;

import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/22 15:05
 * @Description: 是否开启社交功能
 */
@Data
public class EnableSocialDTO {
    /**
     * 用户id
     */
    private long userId;
    /**
     * 是否开启社交功能
     */
    private Integer enableSocial;
}
