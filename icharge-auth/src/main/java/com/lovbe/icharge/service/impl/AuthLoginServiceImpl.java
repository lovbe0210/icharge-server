package com.lovbe.icharge.service.impl;

import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.exception.ServiceErrorCodeConstants;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.entity.UserInfo;
import com.lovbe.icharge.common.util.FeignRequestUtils;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.entity.dto.code.AuthCodeReqDTO;
import com.lovbe.icharge.entity.vo.AuthMobileCodeLoginReqVo;
import com.lovbe.icharge.entity.vo.resp.AuthLoginRespVo;
import com.lovbe.icharge.enums.CodeSceneEnum;
import com.lovbe.icharge.enums.LoginLogTypeEnum;
import com.lovbe.icharge.service.AuthCodeService;
import com.lovbe.icharge.service.AuthService;
import com.lovbe.icharge.service.feign.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthLoginServiceImpl implements AuthService {
    @Resource
    private AuthCodeService codeService;
    @Resource
    private UserService userService;

    @Override
    public AuthLoginRespVo smsLogin(BaseRequest<AuthMobileCodeLoginReqVo> reqVo) {
        // 校验验证码
        String userIp = ServletUtils.getClientIP();
        AuthMobileCodeLoginReqVo data = reqVo.getData();
        codeService.useSmsCode(new AuthCodeReqDTO()
                                    .setMobile(data.getMobile())
                                    .setCode(data.getCode())
                                    .setScene(CodeSceneEnum.MOBILE_LOGIN)
                                    .setUsedIp(userIp));

        // 获得获得注册用户
        ResponseBean<UserInfo> userInfoResp = userService.createUserIfAbsent(data.getMobile(), userIp);
        if (!FeignRequestUtils.checkResp(userInfoResp)) {
           throw new ServiceException(ServiceErrorCodeConstants.USER_NOT_EXISTS);
        }
        UserInfo user = userInfoResp.getData();

        // 校验是否禁用
        if (!CommonStatusEnum.isNormal(user.getStatus())) {
            throw new ServiceException(ServiceErrorCodeConstants.AUTH_LOGIN_USER_DISABLED);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user, data.getMobile(), LoginLogTypeEnum.LOGIN_SMS_CODE, userIp);
    }

    private AuthLoginRespVo createTokenAfterLoginSuccess(UserInfo user, String mobile, LoginLogTypeEnum logType, String userIp) {
        // 插入登陆日志 TODO
//        createLoginLog(user.getUid(), mobile, logType, LoginResultEnum.SUCCESS);
        // 创建 Token 令牌

        // 构建返回结果
        return AuthLoginRespVo.builder().accessToken("").build();
    }
}
