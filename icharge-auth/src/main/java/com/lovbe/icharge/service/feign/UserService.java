package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.entity.UserInfo;

public interface UserService {
    public ResponseBean<UserInfo> createUserIfAbsent(String mobile, String userIp);
}
