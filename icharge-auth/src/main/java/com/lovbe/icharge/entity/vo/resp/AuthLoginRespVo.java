package com.lovbe.icharge.entity.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 登陆返回内容
 * @author: Lvhl
 * @date: 2024/8/7 13:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginRespVo {
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 仅社交登录、社交绑定时会返回
     */
    private String openid;
}
