package com.lovbe.icharge.util;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

/**
 * 安全服务工具类
 *
 * copy from yudao-spring-boot-starter-security 的 SecurityFrameworkUtils 类
 *
 * @author 芋道源码
 */
@Slf4j
public class SecurityFrameworkUtils {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String AUTHORIZATION_BEARER = "Bearer";

    public static final String LOGIN_USER_HEADER = "login-user";

    public static final String LOGIN_USER_ID_ATTR = "login-user-id";
    public static final String LOGIN_USER_TYPE_ATTR = "login-user-type";

    private SecurityFrameworkUtils() {}

    /**
     * 从请求中，获得认证 Token
     *
     * @param exchange 请求
     * @return 认证 Token
     */
    public static String obtainAuthorization(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        int index = authorization.indexOf(AUTHORIZATION_BEARER + " ");
        if (index == -1) { // 未找到
            return null;
        }
        return authorization.substring(index + 7).trim();
    }

    /**
     * 移除请求头的用户
     *
     * @param exchange 请求
     * @return 请求
     */
    public static ServerWebExchange removeLoginUser(ServerWebExchange exchange) {
        // 如果不包含，直接返回
        if (!exchange.getRequest().getHeaders().containsKey(LOGIN_USER_HEADER)) {
            return exchange;
        }
        // 如果包含，则移除。参考 RemoveRequestHeaderGatewayFilterFactory 实现
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(httpHeaders -> httpHeaders.remove(LOGIN_USER_HEADER)).build();
        return exchange.mutate().request(request).build();
    }

    /**
     * 获得登录用户的编号
     *
     * @param exchange 请求
     * @return 用户编号
     */
    public static Long getLoginUserId(ServerWebExchange exchange) {
        return MapUtil.getLong(exchange.getAttributes(), LOGIN_USER_ID_ATTR);
    }
}
