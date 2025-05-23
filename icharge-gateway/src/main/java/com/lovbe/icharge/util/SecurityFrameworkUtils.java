package com.lovbe.icharge.util;

import cn.hutool.core.map.MapUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
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

    public static final String AUTHORIZATION_HEADER = "token";

    public static final String AUTHORIZATION_BEARER = "Bearer";

    public static final String LOGIN_USER_HEADER = "login-user";

    public static final String HEADER_USER_ID = "userId";
    public static final String LOGIN_USER_TYPE_ATTR = "login-user-type";

    private SecurityFrameworkUtils() {}

    /**
     * 从请求中，获得认证 Token
     *
     * @param exchange 请求
     * @return 认证 Token
     */
    public static String obtainAuthToken(ServerWebExchange exchange) {
        MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
        HttpCookie acToken = cookies.getFirst("icharge_actoken");
        String token = null;
        if (acToken != null) {
            token = acToken.getValue();
        }
        return token;
    }

    /**
     * 移除请求头的用户
     *
     * @param exchange 请求
     * @return 请求
     */
    public static ServerWebExchange removeLoginUser(ServerWebExchange exchange) {
        // 如果不包含，直接返回
        if (!exchange.getRequest().getHeaders().containsKey(HEADER_USER_ID)) {
            return exchange;
        }
        // 如果包含，则移除。参考 RemoveRequestHeaderGatewayFilterFactory 实现
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(httpHeaders -> httpHeaders.remove(HEADER_USER_ID)).build();
        return exchange.mutate().request(request).build();
    }

    /**
     * 获得登录用户的编号
     *
     * @param exchange 请求
     * @return 用户编号
     */
    public static Long getLoginUserId(ServerWebExchange exchange) {
        return MapUtil.getLong(exchange.getAttributes(), HEADER_USER_ID);
    }

    /**
     * 将 user 并设置到 login-user 的请求头，使用 json 存储值
     *
     * @param builder 请求
     * @param userId 用户id
     */
    @SneakyThrows
    public static void setLoginUserHeader(ServerHttpRequest.Builder builder, String userId) {
        try {
            builder.header(HEADER_USER_ID, userId);
        } catch (Exception ex) {
            log.error("[setLoginUserHeader] --- 设置请求头userId: {}失败，errorInfo：{}", userId, ex.toString());
            throw ex;
        }
    }
}
