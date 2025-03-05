package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.DesensitizedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.LoginLogTypeEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.config.ServiceProperties;
import com.lovbe.icharge.dao.UserMapper;
import com.lovbe.icharge.entity.dto.ForgetPasswordDTO;
import com.lovbe.icharge.dao.AccountMapper;
import com.lovbe.icharge.entity.dto.UpdateAccountDTO;
import com.lovbe.icharge.entity.vo.AccountVo;
import com.lovbe.icharge.service.AccountService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lovbe
 * @Date: 2024/8/13 23:12
 * @Description: MS
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private BCryptPasswordEncoder cryptPasswordEncoder;
    @Resource
    private CommonService commonService;
    @Resource
    private ServiceProperties properties;
    @Resource
    private RedissonClient redissonClient;

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
    public void resetUserPwd(ForgetPasswordDTO data) {
        // 校验验证码是否正确
        boolean isMobile = CodeSceneEnum.sceneIsMobile(data.getScene());
        String payload = isMobile ? data.getMobile() : data.getEmail();
        String codeExpireKey = RedisKeyConstant.getCodeControlKey(payload);
        Object codeExpire = RedisUtil.hget(codeExpireKey, data.getVerifyCode());
        if (codeExpire == null) {
            throw new ServiceException(ServiceErrorCodes.AUTH_CODE_ERROR);
        }
        // 中间的为过期时间
        String[] split = ((String) codeExpire).split(SysConstant.SEPARATOR);
        Long expire = Long.valueOf(split[1]);
        if (System.currentTimeMillis() > expire) {
            throw new ServiceException(ServiceErrorCodes.AUTH_CODE_EXPIRED);
        }

        // 判断用户是否有效
        AccountDo account = getAccountByMobileOrEmail(data);
        if (account == null) {
            throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
        }

        // 密码加密入库
        String decodedPassword = Base64.decodeStr(CommonUtils.bitwiseInvert(data.getPassword()));
        String encodePassword = cryptPasswordEncoder.encode(decodedPassword);
        account.setPassword(encodePassword);
        int updated = updateAccount(account);
        if (updated == 0) {
            account.setPassword("***").setMobile("***").setEmail("***");
            log.error("[重置密码] --- 密码重置失败，account: {}", account);
            throw new ServiceException(ServiceErrorCodes.ACCOUNT_PASSWORD_RESET_FAILED);
        }
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

    @Override
    public void updateAccountInfo(UpdateAccountDTO accountDTO, Long userId) {
        CodeSceneEnum codeByScene = CodeSceneEnum.getCodeByScene(accountDTO.getScene());
        if (codeByScene == null) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        AccountDo accountDo = accountMapper.selectById(userId);
        if (accountDo == null || !CommonStatusEnum.isNormal(accountDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
        }
        String decodedStr = Base64.decodeStr(CommonUtils.bitwiseInvert(accountDTO.getSign()));
        String encryString = CommonUtils.encryptStr(decodedStr);
        switch (codeByScene) {
            case VERIFY_MOBILE -> {
                // 通过手机号获取相同手机账号
                Long mobileCount = accountMapper.selectCount(new LambdaQueryWrapper<AccountDo>()
                        .eq(AccountDo::getMobile, encryString)
                        .eq(AccountDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
                if (mobileCount > 0) {
                    throw new ServiceException(ServiceErrorCodes.USER_MOBILE_USED);
                }
                accountDo.setMobile(encryString);
            }
            case VERIFY_EMAIL -> {
                // 通过邮箱号获取相同邮箱账号
                Long emailCount = accountMapper.selectCount(new LambdaQueryWrapper<AccountDo>()
                        .eq(AccountDo::getEmail, encryString)
                        .eq(AccountDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
                if (emailCount > 0) {
                    throw new ServiceException(ServiceErrorCodes.USER_EMAIL_USED);
                }
                accountDo.setEmail(encryString);
            }
            case EMAIL_UPDATE_PASSWORD -> accountDo.setPassword(cryptPasswordEncoder.encode(decodedStr));
            case UPDATE_DOMAIN -> {
                updateUserDomain(userId, decodedStr);
                return;
            }
            default -> throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        updateAccount(accountDo);
    }

    /**
     * @description: 更新用户信息个人主页路径
     * @param: userId
     * @param: decodedStr
     * @author: lovbe0210
     * @date: 2025/2/3 11:59
     */
    private void updateUserDomain(Long userId, String decodedStr) {
        UserInfoDo userInfoDo = userMapper.selectById(userId);
        if (userInfoDo == null || !CommonStatusEnum.isNormal(userInfoDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
        }
        RLock lock = redissonClient.getLock(decodedStr);
        try {
            if (lock.tryLock(5, -1, TimeUnit.SECONDS)) {
                Long count = userMapper.selectCount(new LambdaQueryWrapper<UserInfoDo>()
                        .eq(UserInfoDo::getDomain, decodedStr)
                        .eq(UserInfoDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
                boolean contains = properties.getDomainFilter().contains(decodedStr);
                if (contains || count > 0) {
                    throw new ServiceException(ServiceErrorCodes.USER_DOMAIN_USED);
                }
                // 如果当前用户的domain不为空，则删除redis中的校验项
                if (StringUtils.hasLength(userInfoDo.getDomain())) {
                    String domainKey = RedisKeyConstant.getDomainKey();
                    boolean hsetted = RedisUtil.hsetIfAbsent(domainKey, decodedStr, userId);
                    if (!hsetted) {
                        throw new ServiceException(ServiceErrorCodes.USER_DOMAIN_USED);
                    }
                    RedisUtil.hdel(domainKey, userInfoDo.getDomain());
                }
                userInfoDo.setDomain(decodedStr);
                userMapper.updateById(userInfoDo);
                String cacheUserKey = RedisKeyConstant.getCacheUserKey(userId);
                RedisUtil.del(cacheUserKey);
            }
        } catch (InterruptedException e) {
            log.error("[更新账号信息] --- 个人主页路径修改失败，errorInfo: {}", e.toString());
            throw new ServiceException(ServiceErrorCodes.USER_INFO_UPDATE_FAILED);
        } catch (ServiceException e) {
            throw e;
        }finally {
            try {
                lock.unlock();
            }catch (Exception e) {
                log.error("[更新账号信息] --- 个人主页路径修改失败，释放domain校验锁异常，errorInfo: {}", e.toString());
            }
        }
    }
}
