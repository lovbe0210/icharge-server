
package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.model.vo.BindingCodeReqVo;
import com.lovbe.icharge.common.model.vo.EmailCodeReqVo;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;
import com.lovbe.icharge.service.SimpleCodeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    @PostMapping("/code/verify")
    public ResponseBean useVerifyCode(@RequestBody @Valid BaseRequest<SimpleCodeReqDTO> codeReqDTO,
                                      @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        codeService.useVerifyCode(codeReqDTO.getData(), userId);
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
    public ResponseBean sendSmsCode(@RequestBody @Valid BaseRequest<SmsCodeReqVo> reqVo,
                                    @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        codeService.sendSmsCode(reqVo.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * description: 发送邮箱验证码
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/email/code")
    public ResponseBean sendEmailCode(@RequestBody @Valid BaseRequest<EmailCodeReqVo> reqVo,
                                      @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        codeService.sendEmailCode(reqVo.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * description: 通过绑定手机或邮箱发送验证码
     * @author: Lvhl
     * @date: 2024/8/2 17:49
     * @param reqVo
     * @return ResponseBean
     */
    @PostMapping("/binding/code")
    public ResponseBean sendBandingCode(@RequestBody @Valid BaseRequest<BindingCodeReqVo> reqVo,
                                        @RequestHeader(SysConstant.USERID) Long userId) {
        codeService.sendBandingCode(reqVo.getData(), userId);
        return ResponseBean.ok();
    }
}
