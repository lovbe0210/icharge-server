package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.enums.LoginLogTypeEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.entity.dto.ForgetPasswordDTO;
import com.lovbe.icharge.dao.AccountMapper;
import com.lovbe.icharge.entity.vo.AccountVo;
import com.lovbe.icharge.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author: lovbe
 * @Date: 2024/8/13 23:12
 * @Description: MS
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private CommonService commonService;

    @Override
    public AccountDo getAccountInfo(AuthUserDTO authUserDTO) {
        // 邮箱手机号加密
        if (StringUtils.hasLength(authUserDTO.getEmail())) {
            authUserDTO.setEmail(CommonUtils.encryptStr(authUserDTO.getEmail()));
        }
        if (StringUtils.hasLength(authUserDTO.getMobile())) {
            authUserDTO.setMobile(CommonUtils.encryptStr(authUserDTO.getMobile()));
        }
        AccountDo accountDo = accountMapper.selectOne(new LambdaQueryWrapper<AccountDo>()
                .eq(AccountDo::getStatus, "A")
                .eq(LoginLogTypeEnum.LOGIN_MOBILE_PASSWORD.getType() == authUserDTO.getLoginType(), AccountDo::getMobile, authUserDTO.getMobile())
                .eq(LoginLogTypeEnum.LOGIN_SMS_CODE.getType() == authUserDTO.getLoginType(), AccountDo::getMobile, authUserDTO.getMobile())
                .eq(LoginLogTypeEnum.LOGIN_EMAIL_PASSWORD.getType() == authUserDTO.getLoginType(), AccountDo::getEmail, authUserDTO.getEmail())
                .eq(LoginLogTypeEnum.LOGIN_EMAIL_CODE.getType() == authUserDTO.getLoginType(), AccountDo::getEmail, authUserDTO.getEmail())
        );
        return accountDo;
    }

    @Override
    public AccountDo getAccountByMobileOrEmail(ForgetPasswordDTO forgetPasswordDTO) {
        if (StringUtils.hasLength(forgetPasswordDTO.getEmail())) {
            forgetPasswordDTO.setEmail(CommonUtils.encryptStr(forgetPasswordDTO.getEmail()));
        }
        if (StringUtils.hasLength(forgetPasswordDTO.getMobile())) {
            forgetPasswordDTO.setMobile(CommonUtils.encryptStr(forgetPasswordDTO.getMobile()));
        }
        AccountDo accountDo = accountMapper.selectOne(new LambdaQueryWrapper<AccountDo>()
                .eq(AccountDo::getStatus, "A")
                .eq(CodeSceneEnum.sceneIsMobile(forgetPasswordDTO.getScene()), AccountDo::getMobile, forgetPasswordDTO.getMobile())
                .eq(CodeSceneEnum.sceneIsEmail(forgetPasswordDTO.getScene()), AccountDo::getEmail, forgetPasswordDTO.getEmail())
        );
        return accountDo;
    }

    @Override
    public void createAccount(AccountDo account) {
        if (StringUtils.hasLength(account.getEmail())) {
            account.setEmail(CommonUtils.encryptStr(account.getEmail()));
        }
        if (StringUtils.hasLength(account.getMobile())) {
            account.setMobile(CommonUtils.encryptStr(account.getMobile()));
        }
        accountMapper.insert(account);
    }

    @Override
    public int updateAccount(AccountDo account) {
        int updated = accountMapper.updateById(account);
        return updated;
    }

    @Override
    public AccountVo getAccountSettings(Long userId) {
        AccountDo accountDo = accountMapper.selectById(userId);
        AccountVo accountVo = new AccountVo();
        BeanUtil.copyProperties(accountDo, accountVo);
        // 手机号和邮箱解密脱敏
        if (StringUtils.hasLength(accountVo.getEmail())) {
            String email = CommonUtils.decryptStr(accountVo.getEmail());
            accountVo.setEmail(DesensitizedUtil.email(email));
        }
        if (StringUtils.hasLength(accountVo.getMobile())) {
            String mobile = CommonUtils.decryptStr(accountVo.getMobile());
            accountVo.setMobile(DesensitizedUtil.mobilePhone(mobile));
        }
        if (StringUtils.hasLength(accountVo.getPassword())) {
            accountVo.setPassword("**********");
        }
        UserInfoDo userInfoDo = commonService.getCacheUser(userId);
        accountVo.setDomain(userInfoDo.getDomain());
        return accountVo;
    }
}
