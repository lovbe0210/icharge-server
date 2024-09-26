package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.common.model.vo.EmailCodeReqVo;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user", fallback = UserServiceFeignFallback.class)
public interface UserService {

    @PostMapping("/api/user/createUserIfAbsent")
    ResponseBean<LoginUser> createUserIfAbsent(BaseRequest<AuthUserDTO> authUserDTO);

    @PostMapping("/api/user/getUser/loginPayload")
    ResponseBean<LoginUser> getLoginUserByPayload(BaseRequest<AuthUserDTO> authUserDTO);

    @PostMapping("/api/user/useVerifyCode")
    ResponseBean useVerifyCode(BaseRequest<SimpleCodeReqDTO> simpleCodeReqDTO);

    @PostMapping("/api/user/mobile/code")
    ResponseBean sendSmsCode(BaseRequest<SimpleCodeReqDTO> codeReqDTO);

    @PostMapping("/api/user/email/code")
    ResponseBean sendEmailCode(BaseRequest<SimpleCodeReqDTO> codeReqDTO);
}
