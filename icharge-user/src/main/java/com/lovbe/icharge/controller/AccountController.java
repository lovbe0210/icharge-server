package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.ForgetPasswordDTO;
import com.lovbe.icharge.entity.dto.UpdateAccountDTO;
import com.lovbe.icharge.entity.vo.AccountVo;
import com.lovbe.icharge.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/29 11:23
 * @Description: MS
 */
@RestController
public class AccountController {
    @Resource
    private AccountService accountService;

    /**
     * @description: 获取账号设置项
     * @param: String
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:38
     */
    @GetMapping("/account/settings")
    public ResponseBean<AccountVo> getAccountSettings(@RequestHeader(SysConstant.USERID) Long userId) {
        return ResponseBean.ok(accountService.getAccountSettings(userId));
    }

    /**
     * @description: 密码重置
     * @param: BaseRequest<ForgetPasswordDTO>
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:38
     */
    @PostMapping("/reset/password")
    public ResponseBean resetUserPwd(@RequestBody @Validated BaseRequest<ForgetPasswordDTO> forgetPwdDto) {
        ForgetPasswordDTO pwdDtoData = forgetPwdDto.getData();
        boolean isMobile = CodeSceneEnum.sceneIsMobile(pwdDtoData.getScene());
        if (isMobile) {
            Assert.notNull(pwdDtoData.getMobile(), SysConstant.NOT_EMPTY_MOBILE);
        }else {
            Assert.notNull(pwdDtoData.getEmail(), SysConstant.NOT_EMPTY_EMAIL);
        }
        accountService.resetUserPwd(forgetPwdDto.getData());
        return ResponseBean.ok();
    }

    /**
     * @description: 账号信息修改
     * @param: baseRequest
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/1 18:47
     */
    @PostMapping("/account/update")
    public ResponseBean updateAccountInfo(@RequestBody BaseRequest<UpdateAccountDTO> baseRequest,
                                          @RequestHeader(SysConstant.USERID) Long userId) {
        accountService.updateAccountInfo(baseRequest.getData(), userId);
        return ResponseBean.ok();
    }
}
