package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFeignFallback extends BaseFallback implements UserService{

    @Override
    public ResponseBean<LoginUser> createUserIfAbsent(AuthUserDTO authUserDTO) {
        return getFallBack();
    }

    @Override
    public ResponseBean<LoginUser> getLoginUserByPayload(AuthUserDTO authUserDTO) {
        return getFallBack();
    }
}
