
package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.model.vo.EmailCodeReqVo;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;
import com.lovbe.icharge.service.SimpleCodeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 简单验证码相关ctl
 * @author: Lvhl
 * @date: 2024/7/9 11:33
 */
@RestController
public class SimpleCodeController {
    @Resource
    private SimpleCodeService codeService;


    /**
     * @description 验证码使用
     * @param[1] codeReqDTO
     * @return ResponseBean
     * @author lovbe0210
     * @date 2024/8/23 16:22
     */
    @PostMapping("/useVerifyCode")
    public ResponseBean useVerifyCode(@RequestBody @Valid BaseRequest<SimpleCodeReqDTO> codeReqDTO) {
        codeService.useVerifyCode(codeReqDTO.getData());
        return ResponseBean.ok();
    }

    /**
     * description: 发送手机验证码
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/mobile/code")
    public ResponseBean sendSmsCode(@RequestBody @Valid BaseRequest<SmsCodeReqVo> reqVo) {
        return ResponseBean.ok(codeService.sendSmsCode(reqVo.getData()));
    }

    /**
     * description: 发送邮箱验证码
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/email/code")
    public ResponseBean sendEmailCode(@RequestBody @Valid BaseRequest<EmailCodeReqVo> reqVo) {
        return ResponseBean.ok(codeService.sendEmailCode(reqVo.getData()));
    }
}
