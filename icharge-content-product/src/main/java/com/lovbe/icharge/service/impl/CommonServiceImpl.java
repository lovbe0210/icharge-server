package com.lovbe.icharge.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.entity.dto.MenuDTO;
import com.lovbe.icharge.dao.CommonDao;
import com.lovbe.icharge.service.CommonService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/24 23:50
 * @Description: MS
 */
@Service
public class CommonServiceImpl implements CommonService {
    @Resource
    private CommonDao commonDao;

    @Override
    public List<MenuDTO> getMenuList() {
        return commonDao.selectList(new LambdaQueryWrapper<MenuDTO>()
                .eq(MenuDTO::getStatus, "A")
                .orderByAsc(MenuDTO::getType)
                .orderByAsc(MenuDTO::getSort));
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

    @Override
    public Integer getRouterDirection(String dynamicId) {
        Integer type = commonDao.selectUriType(dynamicId);
        return type;
    }
}
