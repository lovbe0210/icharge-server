package com.lovbe.icharge.filter.security;


import cn.hutool.core.util.StrUtil;
import com.lovbe.icharge.config.GatewayConfigProperties;
import com.lovbe.icharge.entity.ResponseBean;
import com.lovbe.icharge.util.RedisUtil;
import com.lovbe.icharge.util.SecurityFrameworkUtils;
import com.lovbe.icharge.util.WebFrameworkUtils;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

/**
 * 执行顺序：3
 * Token 过滤器，验证 token 的有效性
 * 1. 验证通过时，将 userId、userType、tenantId 通过 Header 转发给服务
 * 2. 验证不通过，还是会转发给服务。因为，接口是否需要登录的校验，还是交给服务自身处理
 *
 * @author 芋道源码
 */
@Data
@Component
public class TokenAuthenticationFilter implements GlobalFilter, Ordered {
    /**
     * 接口请求白名单，无需校验token
     */
    @Resource
    private GatewayConfigProperties configProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, GatewayFilterChain chain) {
        // 移除 login-user 的请求头，避免伪造模拟
        SecurityFrameworkUtils.removeLoginUser(exchange);

        // 判断请求路径是否是无需登录
        String token = SecurityFrameworkUtils.obtainAuthToken(exchange);
        Set<String> whiteList = configProperties.getWhiteList();
        whiteList = CollectionUtils.isEmpty(whiteList) ? Collections.EMPTY_SET : whiteList;
        String path = exchange.getRequest().getPath().toString();
        for (String whiteUrl : whiteList) {
            if (antPathMatcher.match(whiteUrl, path)) {
                return chain.filter(exchange);
            }
        }

        // token判断
        if (StrUtil.isEmpty(token)) {
            return WebFrameworkUtils.writeJSON(exchange, ResponseBean.error(401, "Authentication failed"));
        }
        // 如果有 Token 令牌，则解析对应 userId 字段，并通过 通过 Header 转发给服务
        // 从缓存中，获取 LoginUser
        String loginUserIdKey = RedisUtil.getAccessTokenKey(token);
        Object userId = RedisUtil.get(loginUserIdKey);
        if (userId == null) {
            return WebFrameworkUtils.writeJSON(exchange, ResponseBean.error(401, "Authentication failed"));
        }
        // 将 userid 设置到 login-user-id 的请求头
        ServerWebExchange newExchange = exchange.mutate()
                .request(builder -> SecurityFrameworkUtils.setLoginUserHeader(builder, String.valueOf(userId))).build();
        return chain.filter(newExchange);
    }

    @Override
    public int getOrder() {
        return -100; // 和 Spring Security Filter 的顺序对齐
    }

}
