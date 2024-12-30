package com.lovbe.icharge.common.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.dao.CommonDao;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.KafkaMessage;
import com.lovbe.icharge.common.model.dto.MenuDTO;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.SpringContextUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/20 20:20
 * @Description: 公共服务
 */
@Slf4j
@Lazy
@Service
public class CommonServiceImpl implements CommonService {
    @Resource
    private CommonDao commonDao;
    @Override
    public UserInfoDo getCacheUser(Long userId) {
        String cacheUserKey = RedisKeyConstant.getCacheUserKey(userId);
        UserInfoDo cacheUser = (UserInfoDo) RedisUtil.get(cacheUserKey);
        if (cacheUser == null) {
            // 获取用户信息
            cacheUser = commonDao.getUserById(userId);
            cacheUser = CommonUtils.checkUserStatus(cacheUser);
            RedisUtil.set(cacheUserKey, cacheUser, SysConstant.DAY_30);
        } else {
          RedisUtil.expire(cacheUserKey, SysConstant.DAY_30);
        }
        return cacheUser;
    }

    @Override
    public List<MenuDTO> getMenuList() {
        return commonDao.selectList();
    }

    @Override
    public String getBeautifulId() {
        String beautifulIdKey = RedisKeyConstant.getBeautifulKey();
        String beautifulId = IdUtil.nanoId(6);
        boolean hasKey = RedisUtil.hasKey(beautifulIdKey);
        Long expire = RedisUtil.getExpire(beautifulIdKey);
        // key不存在或者key存续时间小于60s都认为key不存在，获取数据库最新数据
        if (!hasKey || expire == null || expire < 60) {
            // 获取数据库中当前user下的所有beautifulId
            List<String> uriList = commonDao.selectAllUri();
            if (CollectionUtils.isEmpty(uriList)) {
                uriList = List.of("lovbe");
            }
            RedisUtil.hputAll(beautifulIdKey,
                    uriList.stream().collect(Collectors.toMap(Function.identity(), Function.identity())));
            RedisUtil.expire(beautifulIdKey, RedisKeyConstant.EXPIRE_7_DAY);
        }
        boolean hsetted = RedisUtil.hsetIfAbsent(beautifulIdKey, beautifulId, null);
        while (!hsetted) {
            beautifulId =  IdUtil.nanoId(6);
            hsetted = RedisUtil.hsetIfAbsent(beautifulIdKey, beautifulId, null);
        }
        return beautifulId;
    }

    /**
     * @description 发送浏览记录消息
     * @param[1] historyDo
     * @author lovbe0210
     * @date 2024/11/24 0:10
     */
    public <T> void sendMessage(String appName, String topic, T t) {
        KafkaMessage<T> message = new KafkaMessage<>(appName, topic, t);
        try {
            KafkaTemplate kafkaTemplate = SpringContextUtils.getBean(KafkaTemplate.class);
            CompletableFuture send = kafkaTemplate.send(topic, JSONUtil.toJsonStr(message));
            send.thenAccept(result -> {
                log.info("[send-message]--消息发送成功， sid：{}", message.getMsgId());
            }).exceptionally(ex -> {
                log.error("[send-message]--消息发送失败，cause: {}, sendData: {}", ex.toString(), JSONUtil.toJsonStr(message));
                return null;
            });
        } catch (Exception e) {
            log.error("[send-message]--消息发送失败，kafka服务不可用, sendData: {}, errorInfo: {}", JSONUtil.toJsonStr(message), e.toString());
        }
    }
}
