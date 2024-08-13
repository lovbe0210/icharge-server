package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;

/**
 * @Author: lovbe
 * @Date: 2024/8/13 22:36
 * @Description: 用户相关接口
 */
public interface UserService {
    /**
     * @description: 获取登录用户信息，如果不存在就创建用户
     * @param: AuthUserDTO
     * @return: LoginUser
     * @author: lovbe0210
     * @date: 2024/8/13 22:37
     */
    LoginUser createUserIfAbsent(AuthUserDTO authUserDTO);
}
