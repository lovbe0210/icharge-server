package com.lovbe.icharge.service;

import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.model.vo.BindingCodeReqVo;
import com.lovbe.icharge.common.model.vo.EmailCodeReqVo;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;
import com.lovbe.icharge.entity.dto.CodeLogDo;
import com.lovbe.icharge.entity.dto.CodeSendDTO;

import java.util.List;

/**
 * @description: 验证码相关接口
 * @author: Lvhl
 * @date: 2024/8/7 14:06
 */
public interface SimpleCodeService {
    /**
     * 发送短信验证码
     *
     * @param reqVo  发送请求
     * @param userId
     * @return
     */
    void sendSmsCode(SmsCodeReqVo reqVo, Long userId);

    /**
     * description: 发送邮箱验证码
     *
     * @param reqVo
     * @param userId
     * @return void
     * @author: Lvhl
     * @date: 2024/8/7 15:23
     */
    void sendEmailCode(EmailCodeReqVo reqVo, Long userId);

    /**
     * 验证验证码，并进行使用
     * 如果正确，则将验证码标记成已使用
     * 如果错误，则抛出 {@link ServiceException} 异常
     *
     * @param reqDTO 使用请求
     * @param userId
     */
    void useVerifyCode(SimpleCodeReqDTO reqDTO, Long userId) throws ServiceException;

    /**
     * @description: 根据账号绑定的手机或邮箱和使用场景发送验证码
     * @param: data
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/1/30 15:46
     */
    void sendBandingCode(BindingCodeReqVo data, Long userId);

    /**
     * @description: 异步发送验证码
     * @param: collect
     * @author: lovbe0210
     * @date: 2025/3/25 22:01
     */
    void handlerSendVerifyCode(List<CodeSendDTO> collect);
}
