package com.lovbe.icharge.util;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public static boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 44
     * 根据key 获取过期时间
     * 45
     *
     * @param key 键 不能为null
     *            46
     * @return 时间(秒) 返回0代表为永久有效
     * 47
     */
    public static long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public static double mapIncr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * @param key     前缀+考试计划
     * @param item
     * @param by
     * @param codes   机构
     * @param scences
     * @return 前缀-考试计划-机构代码-场次码
     */
    public static double mapIncr(String key, String item, double by, List<String> codes, Set<String> scences) {
        if (scences == null || scences.size() == 0) {
            return 0.0;
        }
        if (codes == null || codes.size() == 0) {
            return 0.0;
        }
        for (String s : scences) {
            if (s != null) {
                for (String code : codes) {
                    if (code != null) {
                        redisTemplate.opsForHash().increment(key + "-" + code + "-" + s, item, by);
                    }
                }
            }
        }
        return 0.0;
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public static double mapDecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }
    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {

        return key == null ? null : redisTemplate.opsForValue().get(key);
    }


    /**
     * @param keys redisKey
     * @param list 省+場次
     * @Description
     * @Version V1.3.0
     * @Return {@link Map< String, Object>}
     * @Author wangdm
     * @Date 2022/4/7 12:33
     */
    public static <T> Map<String, T> mGet(List<String> keys, List<String> list) {
        Map<String, T> map = new HashMap<>();
        List<Object> pipelinedList = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String hashKey : keys) {
                connection.get(hashKey.getBytes());
            }
            return null;
        });

        if (!CollectionUtils.isEmpty(pipelinedList) && !CollectionUtils.isEmpty(list)) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                map.put(list.get(i), (T) pipelinedList.get(i));
            }
        }
        return map;

    }

    /**
     * 批量获取hash数据
     *
     * @param keys 缓存key
     * @param list 返回结果key
     * @param <V>
     * @return
     */
    public static <V> Map<String, Map<String, V>> mGetMap(List<String> keys, List<String> list) {
        Map<String, Map<String, V>> map = new HashMap<>();
        List<Object> pipelinedList = redisTemplate.executePipelined((RedisCallback<Map<String, Object>>) connection -> {
            for (String hashKey : keys) {
                connection.hGetAll(hashKey.getBytes());
            }
            return null;
        });

        if (!CollectionUtils.isEmpty(pipelinedList) && !CollectionUtils.isEmpty(list)) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                map.put(list.get(i), (Map<String, V>) pipelinedList.get(i));
            }
        }
        return map;
    }

    /**
     * 批量设置常规数据
     *
     * @param map
     */
    public static <T extends Number> void mSet(Map<String, T> map) {
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            map.forEach((k, v) -> {
                connection.set(k.getBytes(), v.toString().getBytes());
            });
            return null;
        }, serializer);
    }

    /**
     * 批量设置hash数据
     *
     * @param map
     * @param <V>
     */
    public static <V> void mHSet(Map<String, Map<String, V>> map) {
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            map.forEach((k, v) -> {
                v.forEach((hk, hv) -> {
                    connection.hSet(k.getBytes(), hk.getBytes(StandardCharsets.UTF_8), hv.toString().getBytes());
                });
            });
            return null;
        }, serializer);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Object get2(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public static <T> T getT(String key) {
        return key == null ? null : (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean setnx(String key) {
        return setnx(key, key, -1);
    }

    public static Boolean setnx(final String key, final Object value, long seconds) {
        return redisTemplate.execute((RedisConnection conn) -> {
            try {
                Boolean nx = redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
                if (nx) {
                    if (!redisTemplate.expire(key, seconds, TimeUnit.SECONDS)) {
                        redisTemplate.delete(key);
                        return Boolean.FALSE;
                    }
                }
                return nx;
            } finally {
                conn.close();
            }
        });
    }

    /**
     * 批量移除hashkey
     *
     * @param key
     * @param hashKeys
     */
    public static void hMdel(final String key, List<String> hashKeys) {
        RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        RedisSerializer hashKeySerializer = redisTemplate.getHashKeySerializer();
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            hashKeys.stream().forEach(hk -> {
                connection.hDel(keySerializer.serialize(key), hashKeySerializer.serialize(hk));
            });
            return null;
        });
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 设置key的过期时间 为第二天
     *
     * @return boolean
     */
    public static boolean setExpireNextDay(String key) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date t = c.getTime();
        return redisTemplate.expireAt(key, t);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key     键
     * @param value   值
     * @param seconds 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set2(String key, Object value, long seconds) {
        try {
            if (seconds > 0) {
                redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public static long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public static long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }
    // ================================Map=================================


    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public static <T> T hgetAndReset(String key, String item) {
        T ret = null;
        List<Object> retList = redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.multi();
                redisOperations.opsForHash().get(key, item);
                redisOperations.opsForHash().delete(key, item);
                return redisOperations.exec();
            }
        });

        if (!CollectionUtils.isEmpty(retList) && null != retList.get(0)) {
            ret = (T) retList.get(0);
        }
        return ret;
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public static <T> T hget(String key, String item) {
        return (T) redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 设置hash的对象的值
     *
     * @param key
     * @param item
     * @param t
     * @param <T>
     * @return
     */
    public static <T> Boolean hNx(String key, String item, T t) {
        return redisTemplate.opsForHash().putIfAbsent(key, item, 0);
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public static <T> T hload(String key, String item, Supplier<T> supplier) {
        T o = (T) redisTemplate.opsForHash().get(key, item);
        if (null == o) {
            o = supplier.get();
            if (null != o) {
                redisTemplate.opsForHash().put(key, item, o);
            }
        }
        return o;
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public static <T> T hload(String key, String item, long expireSeconds, Supplier<T> supplier) {
        return redisTemplate.execute(new SessionCallback<T>() {
            @Override
            public <K, V> T execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                T o = (T) redisTemplate.opsForHash().get(key, item);
                if (null == o) {
                    o = supplier.get();
                    if (null != o) {
                        redisTemplate.opsForHash().put(key, item, o);
                        redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
                    }
                }
                return o;
            }
        });

    }

    /**
     * HashGet
     *
     * @param key      键 不能为null
     * @param supplier 项 不能为null
     * @return 值
     */
    public static <K, V> Map<K, V> hmload(String key, long expireSeconds, Supplier<Map<K, V>> supplier) {
        return redisTemplate.execute(new SessionCallback<Map<K, V>>() {
            @Override
            public <K, V> Map execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                Map o = redisTemplate.opsForHash().entries(key);
                if (CollectionUtils.isEmpty(o)) {
                    o = supplier.get();
                    if (null != o) {
                        redisTemplate.opsForHash().putAll(key, o);
                        redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
                    }
                }
                return o;
            }
        });

    }


    public static <K, V> Map<K, V> hmload(String key, Supplier<Map<K, V>> supplier) {
        return redisTemplate.execute(new SessionCallback<Map<K, V>>() {
            @Override
            public <K, V> Map execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                Map o = redisTemplate.opsForHash().entries(key);
                if (CollectionUtils.isEmpty(o)) {
                    o = supplier.get();
                    if (null != o) {
                        redisTemplate.opsForHash().putAll(key, o);
                    }
                }
                return o;
            }
        });

    }

    /**
     * HSCAN
     *
     * @param key      键 不能为null
     * @param consumer 消费 不能为null
     * @return 值
     */
    public static <K, V> void hscan(String key, Consumer<Map.Entry<K, V>> consumer) {
        Cursor<Map.Entry<Object, Object>> cursor = null;
        try {
            cursor = redisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().match("*").count(1000).build());
            if (null == cursor || !cursor.hasNext()) {
                log.debug("[缓存中没有数据], key={}", key);
                return;
            }
            while (cursor.hasNext()) {
                consumer.accept((Map.Entry<K, V>) cursor.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(cursor);
        }
    }

    /**
     * HSCAN
     *
     * @param key 键 不能为null
     * @return 值
     */
    public static <K, V> List<Map.Entry<K, V>> hscan(String key) {
        Cursor<Map.Entry<Object, Object>> cursor = null;
        List<Map.Entry<K, V>> list = new LinkedList<>();
        try {
            cursor = redisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().match("*").count(1000).build());
            if (null == cursor || !cursor.hasNext()) {
                log.debug("[缓存中没有数据], key={}", key);
                return Collections.emptyList();
            }
            while (cursor.hasNext()) {
                list.add((Map.Entry<K, V>) cursor.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(cursor);
        }
        return list;
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public static <T> Map<String, T> hmget(String key) {
        return redisTemplate.<String, T>opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public static boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public static <E> boolean hmset(String key, Map<String, E> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static boolean hsetIfAbsent(String key, String item, Object value) {
        try {
            return Optional.ofNullable(redisTemplate.opsForHash().putIfAbsent(key, item, value)).orElse(false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public static boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * redis hput , 设置过期时间  ,单位为妙
     *
     * @param key   key
     * @param item  hash key
     * @param value 只
     * @param time  过期时间
     * @return boolean
     */
    public static boolean hPutExpire(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static <T> boolean hput(String key, String item, T value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 批量处理hash
     *
     * @param key
     * @param map
     * @param <T>
     */
    public static <T> void hputAll(String key, Map<String, T> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }


    /**
     * 获取hash的values
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> List<T> hgetAll(String key) {
        return redisTemplate.<String, T>opsForHash().values(key);
    }

    /**
     * 获取hash的values
     *
     * @param key
     * @return
     */
    public static Map<Object, Object> hgetMap(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * @param key
     * @param hashKeys
     * @Description 批量获取 hashKey
     * @Version V1.3.0
     * @Return {@link Map< String,T>}
     * @Author wangdm
     * @Date 2022/3/31 15:00
     */
    public static <T> Map<String, T> hMGet(String key, List<String> hashKeys) {
        List<T> ts = redisTemplate.<String, T>opsForHash().multiGet(key, hashKeys);
        Map<String, T> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(ts) && !CollectionUtils.isEmpty(hashKeys)) {
            int size = hashKeys.size();
            for (int i = 0; i < size; i++) {
                map.put(hashKeys.get(i), ts.get(i));
            }
        }
        return map;
    }

    /**
     * @param redisKeys
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> hMGet(List<String> redisKeys, List<String> list) {
        Map<String, T> map = new ConcurrentHashMap<>();
        List<Object> pipelinedList = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String key : redisKeys) {
                connection.hGetAll(key.getBytes());
            }
            return null;
        });

        if (!CollectionUtils.isEmpty(pipelinedList) && !CollectionUtils.isEmpty(list)) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                map.put(list.get(i), (T) pipelinedList.get(i));
            }
        }
        return map;
    }

    /**
     * @param mapHashMap
     * @param <T>
     * @return
     */
    public static <R, T> void hMput(Map<String, Map<R, T>> mapHashMap) {

        RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        RedisSerializer valueSerializer = redisTemplate.getValueSerializer();

        List<Object> pipelinedList = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            mapHashMap.forEach((k, v) -> {
                v.forEach((hk, hv) -> {
                    connection.hSet(keySerializer.serialize(k), keySerializer.serialize(hk), valueSerializer.serialize(hv));
                });
            });
            return null;
        });
    }

    /**
     * @param key
     * @param hashKeys
     * @Description 批量判断hashKey 是否存在
     * @Version V1.3.0
     * @Return {@link Map< String, Boolean>}
     * @Author wangdm
     * @Date 2022/4/1 11:51
     */
    public static Map<String, Boolean> hSetNX(String key, List<String> hashKeys) {
        Map<String, Boolean> map = new HashMap<>();
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        RedisSerializer<Object> hashValueSerializer = (RedisSerializer<Object>) redisTemplate.getHashValueSerializer();
        List<Object> pipelinedList = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                //StringRedisConnection stringRedisConnection = ((StringRedisConnection) connection);
                for (String hashKey : hashKeys) {
                    //stringRedisConnection.setNX(key,hashKey);
                    connection.hSetNX(serializer.serialize(key), serializer.serialize(hashKey), hashValueSerializer.serialize('0'));
                }
                return null;
            }
        });

        if (!CollectionUtils.isEmpty(hashKeys) && !CollectionUtils.isEmpty(pipelinedList)) {
            int size = hashKeys.size();
            for (int i = 0; i < size; i++) {
                if (Boolean.valueOf(pipelinedList.get(i).toString())) {
                    map.put(hashKeys.get(i), true);
                } else {
                    map.put(hashKeys.get(i), false);
                }
            }
        }
        return map;
    }

    /**
     * @param key
     * @param map
     * @Description 批量新增
     * @Version V1.3.0
     * @Return {@link }
     * @Author wangdm
     * @Date 2022/4/1 11:52
     */
    public static void hIncrBy(String key, Map<String, AtomicInteger> map) {
        if (CollectionUtils.isEmpty(map)) {
            log.error("无效的redis-value:map, key=" + key);
            return;
        }
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            map.forEach((k, v) -> {
                connection.hIncrBy(serializer.serialize(key), serializer.serialize(String.valueOf(k)), Long.valueOf(v.get()));
            });
            return null;
        }, serializer);
    }

    public static void hIncrByIntNum(String key, Map<String, Integer> map) {
        if (CollectionUtils.isEmpty(map)) {
            log.error("无效的redis-value:map, key=" + key);
            return;
        }
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            map.forEach((k, v) -> {
                connection.hIncrBy(serializer.serialize(key), serializer.serialize(String.valueOf(k)), v);
            });
            return null;
        }, serializer);
    }

    public static void hIncrByNum(String key, Map<String, Long> map) {
        if (CollectionUtils.isEmpty(map)) {
            log.error("无效的redis-value:map, key=" + key);
            return;
        }
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            map.forEach((k, v) -> {
                connection.hIncrBy(serializer.serialize(key), serializer.serialize(String.valueOf(k)), v);
            });
            return null;
        }, serializer);
    }

    /**
     * 获取hash的values
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> List<T> range(String key, int start, int end) {
        List<T> ret = null;
        List<Object> retList = redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                redisTemplate.opsForList().range(key, start, end);
                redisTemplate.opsForList().trim(key, end + 1, -1);
                return operations.exec();
            }
        });
        if (!CollectionUtils.isEmpty(retList) && null != retList.get(0)) {
            ret = (List<T>) retList.get(0);
        }
        if (null == ret) {
            log.debug("列表中没有数据: key={}, start={}, end={}", key, start, end);
        }
        return ret;
    }

    /**
     * 获取hash的values
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> void listAdd(String key, T t) {
        redisTemplate.opsForList().rightPush(key, t);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static <T> boolean hput(String key, String item, T value, long seconds) {
        try {
            redisTemplate.execute(new SessionCallback<List<Object>>() {
                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.opsForHash().put(key, item, value);
                    operations.expire(key, seconds, TimeUnit.SECONDS);
                    return operations.exec();
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public static void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public static boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public static double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public static double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }
    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public static Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public static boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public static long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public static long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public static List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public static long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public static Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public static boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public static boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public static boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public static long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 设置offset位的值
     *
     * @param key
     * @param offset
     * @param val
     * @return
     */
    public static Boolean bitSet(String key, long offset, boolean val) {
        return redisTemplate.opsForValue().setBit(key, offset, val);
    }

    /**
     * 读取offset位的值
     *
     * @param key
     * @param offset
     * @return
     */
    public static Boolean bitGet(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }


    /**
     * 统计已标记的总数
     *
     * @param key
     * @return
     */
    public static Long bitCount(String key) {
        return redisTemplate.execute(conn -> {
            byte[] bytes = ((StringRedisSerializer) redisTemplate.getKeySerializer()).serialize(key);
            return conn.bitCount(bytes);
        }, true);
    }

    /**
     * 获取hash的大小
     *
     * @param key
     * @return
     */
    public static long hlen(String key) {
        byte[] keyByte = ((StringRedisSerializer) redisTemplate.getKeySerializer()).serialize(key);
        return redisTemplate.execute((RedisConnection conn) -> conn.hashCommands().hLen(keyByte));
    }

    public static <T> void zset(String key, long score, T val) {
        redisTemplate.opsForZSet().add(key, val, score);
    }
}
