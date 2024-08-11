package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.entity.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
//@FeignClient(fallback = Exception.class)
public class UserService {
    public ResponseBean<LoginUser> createUserIfAbsent(String mobile, Integer loginType, String userIp){
        return null;
    };

    public ResponseBean<LoginUser> getLoginUserByMobile(String mobile) {
        return null;
    };

    public ResponseBean<LoginUser> getLoginUserByEmail(String email) {
        return null;
    };
}
