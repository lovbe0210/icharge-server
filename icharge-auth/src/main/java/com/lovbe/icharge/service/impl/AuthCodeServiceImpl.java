package com.lovbe.icharge.service.impl;

import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.entity.dto.AuthCodeReqDTO;
import com.lovbe.icharge.service.AuthCodeService;
import org.springframework.stereotype.Service;

@Service
public class AuthCodeServiceImpl implements AuthCodeService {
    @Override
    public void sendMobileCode(AuthCodeReqDTO reqDTO) {

    }

    @Override
    public void sendEmailCode(AuthCodeReqDTO reqDTO) {

    }

    @Override
    public void useSmsCode(AuthCodeReqDTO reqDTO) throws ServiceException {

    }

    @Override
    public void validateSmsCode(AuthCodeReqDTO reqDTO) {

    }
}
