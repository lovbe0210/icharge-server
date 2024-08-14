package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;

/**
 * @Author: lovbe
 * @Date: 2024/8/13 22:36
 * @Description: 账号相关接口
 */
public interface AccountService {
    /**
     * @description: 获取登录用户信息，如果不存在就创建用户
     * @param: AuthUserDTO
     * @return: AccountDo
     * @author: lovbe0210
     * @date: 2024/8/13 22:37
     */
    AccountDo getAccountInfo(AuthUserDTO authUserDTO);

    /** 
     * @description: 创建账号信息
     * @param: AccountDo
     * @return: void
     * @author: lovbe0210
     * @date: 2024/8/14 0:00
     */
    void createAccount(AccountDo account);

    /**
     * @description: 升级账号信息
     * @param: AccountDo
     * @return: void
     * @author: lovbe0210
     * @date: 2024/8/14 20:54
     */
    void updateAccount(AccountDo account);
}
