package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.entity.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service", fallback = Exception.class)
public interface UserService {
    ResponseBean<LoginUser> createUserIfAbsent(String mobile, Integer loginType, String userIp);

    ResponseBean<LoginUser> getLoginUserByMobile(String mobile);

    ResponseBean<LoginUser> getLoginUserByEmail(String email);
}
