package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.common.model.vo.EmailCodeReqVo;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFeignFallback extends BaseFallback implements UserService{

    @Override
    public ResponseBean<LoginUser> createUserIfAbsent(BaseRequest<AuthUserDTO> authUserDTO) {
        return getFallBack();
    }

    @Override
    public ResponseBean<LoginUser> getLoginUserByPayload(BaseRequest<AuthUserDTO> authUserDTO) {
        return getFallBack();
    }

    @Override
    public ResponseBean useVerifyCode(BaseRequest<SimpleCodeReqDTO> simpleCodeReqDTO) {
        return getFallBack();
    }

    @Override
    public ResponseBean sendSmsCode(BaseRequest<SimpleCodeReqDTO> codeReqDTO) {
        return getFallBack();
    }

    @Override
    public ResponseBean sendEmailCode(BaseRequest<SimpleCodeReqDTO> codeReqDTO) {
        return getFallBack();
    }
}
