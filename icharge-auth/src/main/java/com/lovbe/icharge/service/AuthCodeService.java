package com.lovbe.icharge.service;

import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.entity.dto.AuthCodeReqDTO;

/**
 * @description: 验证码相关接口
 * @author: Lvhl
 * @date: 2024/8/7 14:06
 */
public interface AuthCodeService {
    /**
     * 发送短信验证码
     *
     * @param reqDTO 发送请求
     */
    void sendMobileCode(AuthCodeReqDTO reqDTO);

    /**
     * description: 发送邮箱验证码
     * @author: Lvhl
     * @date: 2024/8/7 15:23
     * @param reqDTO
     * @return void
     */
    void sendEmailCode(AuthCodeReqDTO reqDTO);

    /**
     * 验证验证码，并进行使用
     * 如果正确，则将验证码标记成已使用
     * 如果错误，则抛出 {@link ServiceException} 异常
     *
     * @param reqDTO 使用请求
     */
    void useVerifyCode(AuthCodeReqDTO reqDTO) throws ServiceException;

    /**
     * 检查验证码是否有效
     *
     * @param reqDTO 校验请求
     */
    void validateSmsCode(AuthCodeReqDTO reqDTO);
}
