package com.lovbe.icharge.service.impl;

import cn.hutool.core.util.IdUtil;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.mapper.UserMapper;
import com.lovbe.icharge.service.AccountService;
import com.lovbe.icharge.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @Author: lovbe
 * @Date: 2024/8/13 22:38
 * @Description: MS
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private AccountService accountService;
    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginUser createUserIfAbsent(AuthUserDTO authUserDTO) {
        AccountDo account = accountService.getAccountInfo(authUserDTO);
        if (account != null) {
            // 记录登录日志
            account.setLastLoginIp(authUserDTO.getUserIp())
                    .setLastLoginTime(LocalDateTime.now())
                    .setLoginAgent(authUserDTO.getLoginAgent())
                    .setLoginOs(authUserDTO.getLoginOs())
                    .setLoginCount(account.getLoginCount()+1)
                    .setUpdateTime(LocalDateTime.now());
            accountService.updateAccount(account);
            LoginUser loginUser = new LoginUser(account.getMobile(), account.getEmail(), account.getPassword());
            loginUser.setStatus(account.getStatus());
            loginUser.setUid(account.getUid());
            return loginUser;
        }
        long uid = YitIdHelper.nextId();
        // 创建账号
        account = new AccountDo()
                .setMobile(authUserDTO.getMobile())
                .setEmail(authUserDTO.getEmail())
                .setPassword(authUserDTO.getPassword())
                .setLoginCount(1)
                .setLastLoginIp(authUserDTO.getUserIp())
                .setLastLoginTime(LocalDateTime.now());
        account.setUid(uid);
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        accountService.createAccount(account);
        // 创建用户
        UserInfoDo userInfoDo = new UserInfoDo()
                .setUsername("用户"+ IdUtil.nanoId(4))
                .setDomain(IdUtil.nanoId(6));
        userInfoDo.setUid(uid);
        userInfoDo.setCreateTime(LocalDateTime.now());
        userInfoDo.setUpdateTime(LocalDateTime.now());
        userMapper.insert(userInfoDo);
        LoginUser loginUser = new LoginUser(account.getMobile(), account.getEmail(), account.getPassword());
        loginUser.setStatus(CommonStatusEnum.NORMAL.getStatus());
        loginUser.setUid(uid);
        return loginUser;
    }

    @Override
    public LoginUser getLoginUserByPayload(AuthUserDTO authUserDTO) {
        AccountDo account = accountService.getAccountInfo(authUserDTO);
        if (account != null) {
            // 记录登录日志
            account.setLastLoginIp(authUserDTO.getUserIp())
                    .setLastLoginTime(LocalDateTime.now())
                    .setLoginAgent(authUserDTO.getLoginAgent())
                    .setLoginOs(authUserDTO.getLoginOs())
                    .setLoginCount(account.getLoginCount()+1)
                    .setUpdateTime(LocalDateTime.now());
            accountService.updateAccount(account);
            LoginUser loginUser = new LoginUser(account.getMobile(), account.getEmail(), account.getPassword());
            loginUser.setStatus(account.getStatus());
            loginUser.setUid(account.getUid());
            return loginUser;
        }
        return null;
    }
}
