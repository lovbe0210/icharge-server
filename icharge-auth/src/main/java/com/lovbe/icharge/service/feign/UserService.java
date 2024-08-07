package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.entity.LoginUser;

public interface UserService {
    ResponseBean<LoginUser> createUserIfAbsent(String mobile, String userIp);

    ResponseBean<LoginUser> getLoginUserByMobile(String mobile);
}
