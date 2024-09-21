package com.lovbe.icharge.filter.security;


import cn.hutool.core.util.StrUtil;
import com.lovbe.icharge.util.RedisUtil;
import com.lovbe.icharge.util.SecurityFrameworkUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.lovbe.icharge.util.SecurityFrameworkUtils.LOGIN_USER_ID_ATTR;

/**
 * 执行顺序：3
 * Token 过滤器，验证 token 的有效性
 * 1. 验证通过时，将 userId、userType、tenantId 通过 Header 转发给服务
 * 2. 验证不通过，还是会转发给服务。因为，接口是否需要登录的校验，还是交给服务自身处理
 *
 * @author 芋道源码
 */
@Component
public class TokenAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, GatewayFilterChain chain) {
        // 移除 login-user 的请求头，避免伪造模拟
        SecurityFrameworkUtils.removeLoginUser(exchange);

        // 情况一，如果没有 Token 令牌，则直接继续 filter
        String token = SecurityFrameworkUtils.obtainAuthorization(exchange);
        if (StrUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }

        // 情况二，如果有 Token 令牌，则解析对应 userId 字段，并通过 通过 Header 转发给服务
        // 从缓存中，获取 LoginUser
        String loginUserIdKey = RedisUtil.getAccessTokenKey(token);
        Object userId = RedisUtil.get(loginUserIdKey);
        if (userId == null) {
            // 判断请求路径是否是无需登录 TODO
            String string = exchange.getRequest().getPath().toString();
            return chain.filter(exchange);
        }
        exchange.getAttributes().put(LOGIN_USER_ID_ATTR, userId);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100; // 和 Spring Security Filter 的顺序对齐
    }

}
