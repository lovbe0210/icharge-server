package com.lovbe.icharge.common.service;

import com.lovbe.icharge.common.model.dto.UserInfoDo;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/20 20:11
 * @Description: 公共服务
 */
public interface CommonService {
    /**
     * @description: 获取用户缓存信息
     * @param: Long
     * @return: UserInfoDo
     * @author: lovbe0210
     * @date: 2024/12/20 23:49
     */
    UserInfoDo getCacheUser(Long userId);
}
