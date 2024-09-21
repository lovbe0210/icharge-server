package com.lovbe.icharge.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: lovbe0210
 * @Date: 2024/9/21 16:21
 * @Description: MS
 */
@Slf4j
@Component
public final class RedisUtil {


    /**
     * redistemplate
     */
    private static RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier(value = "redisTemplate")
    public void init(RedisTemplate redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
    }

    /**
     * 过期时间，单位都为秒
     */
    public static final long EXPIRE_10_MIN = 60 * 10;
    public static final long EXPIRE_30_MIN = 60 * 30;
    public static final long EXPIRE_1_HOUR = 60 * 60;
    public static final long EXPIRE_2_HOUR = 60 * 60 * 2;

    /**
     * 项目前缀
     */
    public static final String BASE_PROJECT = "icharge:";
    /**
     * 权限认证服务
     */
    public static final String AUTH = "auth:";

    /**
     * @description: 获取accessToken
     * @param: String
     * @return: String
     * @author: lovbe0210
     * @date: 2024/8/18 14:22
     */
    public static String getAccessTokenKey(String accessToken) {
        return BASE_PROJECT + AUTH + "access-token:" + accessToken;
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {

        return key == null ? null : redisTemplate.opsForValue().get(key);
    }
}
