package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.entity.dto.ForgetPasswordDTO;
import com.lovbe.icharge.entity.vo.AccountVo;

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

    AccountDo getAccountByMobileOrEmail(ForgetPasswordDTO forgetPasswordDTO);

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
    int updateAccount(AccountDo account);

    /**
     * @description: 获取账号设置状态
     * @param: userId
     * @return: com.lovbe.icharge.entity.vo.AccountVo
     * @author: lovbe0210
     * @date: 2025/1/29 15:35
     */
    AccountVo getAccountSettings(Long userId);
}
