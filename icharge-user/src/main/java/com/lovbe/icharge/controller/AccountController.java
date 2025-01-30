package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.entity.vo.AccountVo;
import com.lovbe.icharge.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
}
