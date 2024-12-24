package com.lovbe.icharge.common.service.impl;

import com.lovbe.icharge.common.dao.CommonDao;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/20 20:20
 * @Description: 公共服务
 */
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
}
