package com.lovbe.icharge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.enums.LoginLogTypeEnum;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.dto.ForgetPasswordDTO;
import com.lovbe.icharge.mapper.AccountMapper;
import com.lovbe.icharge.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author: lovbe
 * @Date: 2024/8/13 23:12
 * @Description: MS
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;
    @Override
    public AccountDo getAccountInfo(AuthUserDTO authUserDTO) {
        return accountMapper.selectOne(new LambdaQueryWrapper<AccountDo>()
                .eq(AccountDo::getStatus, "A")
                .eq(LoginLogTypeEnum.LOGIN_MOBILE_PASSWORD.getType() == authUserDTO.getLoginType(), AccountDo::getMobile, authUserDTO.getMobile())
                .eq(LoginLogTypeEnum.LOGIN_SMS_CODE.getType() == authUserDTO.getLoginType(), AccountDo::getMobile, authUserDTO.getMobile())
                .eq(LoginLogTypeEnum.LOGIN_EMAIL_PASSWORD.getType() == authUserDTO.getLoginType(), AccountDo::getEmail, authUserDTO.getEmail())
                .eq(LoginLogTypeEnum.LOGIN_EMAIL_CODE.getType() == authUserDTO.getLoginType(), AccountDo::getEmail, authUserDTO.getEmail())
        );
    }

    @Override
    public AccountDo getAccountByMobileOrEmail(ForgetPasswordDTO forgetPasswordDTO) {
        return accountMapper.selectOne(new LambdaQueryWrapper<AccountDo>()
                .eq(AccountDo::getStatus, "A")
                .eq(CodeSceneEnum.sceneIsMobile(forgetPasswordDTO.getScene()), AccountDo::getMobile, forgetPasswordDTO.getMobile())
                .eq(CodeSceneEnum.sceneIsEmail(forgetPasswordDTO.getScene()), AccountDo::getMobile, forgetPasswordDTO.getEmail())
        );
    }

    @Override
    public void createAccount(AccountDo account) {
        accountMapper.insert(account);
    }

    @Override
    public int updateAccount(AccountDo account) {
        return accountMapper.updateById(account);
    }
}
