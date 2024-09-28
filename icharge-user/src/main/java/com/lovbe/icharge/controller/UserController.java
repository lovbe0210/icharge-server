package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.dto.ForgetPasswordDTO;
import com.lovbe.icharge.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户信息相关控制层
 * @author: Lvhl
 * @date: 2024/7/9 11:33
 */
@RestController
public class UserController {
    @Resource
    private UserService userService;

    /**
     * description: 通过登录/注册的方式返回用户信息，如果不存在就创建
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param authUserDTO
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/createUserIfAbsent")
    public ResponseBean<LoginUser> createUserIfAbsent(@RequestBody @Valid BaseRequest<AuthUserDTO> authUserDTO) {
        return ResponseBean.ok(userService.createUserIfAbsent(authUserDTO.getData()));
    }

    /**
     * description: 通过手机号或邮箱获取用户信息
     * @author: Lvhl
     * @date: 2024/9/16 11:59
     * @param authUserDTO
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/getUser/loginPayload")
    public ResponseBean<LoginUser> getLoginUserByPayload(@RequestBody @Valid BaseRequest<AuthUserDTO> authUserDTO) {
        return ResponseBean.ok(userService.getLoginUserByPayload(authUserDTO.getData()));
    }

    @PostMapping("/reset/password")
    public ResponseBean resetUserPwd(@RequestBody @Validated BaseRequest<ForgetPasswordDTO> forgetPwdDto) {
        ForgetPasswordDTO pwdDtoData = forgetPwdDto.getData();
        boolean isMobile = CodeSceneEnum.sceneIsMobile(pwdDtoData.getScene());
        if (isMobile) {
            Assert.notNull(pwdDtoData.getMobile(), SysConstant.NOT_EMPTY_MOBILE);
        }else {
            Assert.notNull(pwdDtoData.getEmail(), SysConstant.NOT_EMPTY_EMAIL);
        }
        userService.resetUserPwd(forgetPwdDto.getData());
        return ResponseBean.ok();
    }
}
