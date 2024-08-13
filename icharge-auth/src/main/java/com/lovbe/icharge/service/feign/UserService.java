package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "icharge-user", fallback = UserServiceFeignFallback.class)
public interface UserService {

    @PostMapping("/api/user/createUserIfAbsent")
    ResponseBean<LoginUser> createUserIfAbsent(AuthUserDTO authUserDTO);

    @PostMapping("/api/user/getUser/byMobile")
    ResponseBean<LoginUser> getLoginUserByMobile(AuthUserDTO authUserDTO);

    @PostMapping("/api/user/getUser/byEmail")
    ResponseBean<LoginUser> getLoginUserByEmail(AuthUserDTO authUserDTO);
}
