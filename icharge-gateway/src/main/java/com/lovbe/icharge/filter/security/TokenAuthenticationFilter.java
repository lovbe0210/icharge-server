package com.lovbe.icharge.filter.security;


import cn.hutool.core.util.StrUtil;
import com.lovbe.icharge.common.model.resp.AuthLoginUser;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.util.SecurityFrameworkUtils;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.lovbe.icharge.util.SecurityFrameworkUtils.LOGIN_USER_ID_ATTR;

/**
 * Token 过滤器，验证 token 的有效性
 * 1. 验证通过时，将 userId、userType、tenantId 通过 Header 转发给服务
 * 2. 验证不通过，还是会转发给服务。因为，接口是否需要登录的校验，还是交给服务自身处理
 *
 * @author 芋道源码
 */
@Component
public class TokenAuthenticationFilter implements GlobalFilter, Ordered {

    /**
     * 空的 LoginUser 的结果
     *
     * 用于解决如下问题：
     * 1. {@link #getLoginUser(ServerWebExchange, String)} 返回 Mono.empty() 时，会导致后续的 flatMap 无法进行处理的问题。
     * 2. {@link #buildUser(String)} 时，如果 Token 已经过期，返回 LOGIN_USER_EMPTY 对象，避免缓存无法刷新
     */
    private static final AuthLoginUser LOGIN_USER_EMPTY = new AuthLoginUser();

    private final WebClient webClient;
/*
    *//**
     * 登录用户的本地缓存
     *
     * key1：多租户的编号
     * key2：访问令牌
     *//*
    private final LoadingCache<Long, AuthLoginUser> loginUserCache = buildAsyncReloadingCache(Duration.ofMinutes(1),
            new CacheLoader<Long, LoginUser>() {
                @Override
                public LoginUser load(Long key) throws Exception {
                    String body = checkAccessToken(token.getKey(), token.getValue()).block();
                    return buildUser(body);
                }
            });*/

    public TokenAuthenticationFilter(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        // Q：为什么不使用 OAuth2TokenApi 进行调用？
        // A1：Spring Cloud OpenFeign 官方未内置 Reactive 的支持 https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/#reactive-support
        // A2：校验 Token 的 API 需要使用到 header[tenant-id] 传递租户编号，暂时不想编写 RequestInterceptor 实现
        // 因此，这里采用 WebClient，通过 lbFunction 实现负载均衡
        this.webClient = WebClient.builder().filter(lbFunction).build();
    }

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
        String loginUserIdKey = RedisKeyConstant.getAccessTokenKey(token);
        Object userId = RedisUtil.get(loginUserIdKey);
        if (userId == null) {
            // 判断请求路径是否是无需登录 TODO
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
