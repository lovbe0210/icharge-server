package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "icharge-user", fallback = UserServiceFeignFallback.class)
public interface UserService {

    @PostMapping("/api/user/createUserIfAbsent")
    ResponseBean<LoginUser> createUserIfAbsent(BaseRequest<AuthUserDTO> authUserDTO);

    @PostMapping("/api/user/getUser/loginPayload")
    ResponseBean<LoginUser> getLoginUserByPayload(BaseRequest<AuthUserDTO> authUserDTO);

    @PostMapping("/useVerifyCode")
    ResponseBean useVerifyCode(BaseRequest<SimpleCodeReqDTO> simpleCodeReqDTO);

    @PostMapping("/mobile/code")
    ResponseBean sendSmsCode(BaseRequest<SimpleCodeReqDTO> codeReqDTO);
}
