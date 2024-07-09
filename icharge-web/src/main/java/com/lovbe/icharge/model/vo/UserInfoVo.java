package com.lovbe.icharge.model.vo;

import lombok.Data;

/**
 * @description: 用户信息
 * @author: Lvhl
 * @date: 2024/7/9 11:37
 */
@Data
public class UserInfoVo {
    /**
     * 用户id
     */
    public Long id;
    public String username;
    public String domain;
    public String level;
    public String avatar;
}
