package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.dto.vo.SecServiceSvVo;
import com.lovbe.icharge.service.SecurityService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lovbe0210
 * @Date: 2024/9/15 8:45
 * @Description: 页面埋点校验相关
 */
@RestController
public class SecurityController {
    @Resource
    private SecurityService securityService;

    /**
     * @description 获取滑块验证码的埋点cookie
     * @param[1] scvo
     * @return ResponseBean
     * @author lovbe0210
     * @date 2024/9/15 8:59
     */
    @PostMapping("/svcookie")
    public ResponseBean setSliderValidCookie(@RequestBody @Valid BaseRequest<SecServiceSvVo> scvo){
        return securityService.setSliderValidCookie(scvo.getData());
    }
}
