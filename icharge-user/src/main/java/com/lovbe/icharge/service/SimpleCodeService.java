package com.lovbe.icharge.service;

import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;

/**
 * @description: 验证码相关接口
 * @author: Lvhl
 * @date: 2024/8/7 14:06
 */
public interface SimpleCodeService {
    /**
     * 发送短信验证码
     *
     * @param reqDTO 发送请求
     * @return
     */
    String sendSmsCode(SmsCodeReqVo reqDTO);

    /**
     * description: 发送邮箱验证码
     * @author: Lvhl
     * @date: 2024/8/7 15:23
     * @param reqDTO
     * @return void
     */
    String sendEmailCode(SimpleCodeReqDTO reqDTO);

    /**
     * 验证验证码，并进行使用
     * 如果正确，则将验证码标记成已使用
     * 如果错误，则抛出 {@link ServiceException} 异常
     *
     * @param reqDTO 使用请求
     */
    void useVerifyCode(SimpleCodeReqDTO reqDTO) throws ServiceException;

}
