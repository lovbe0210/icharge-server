package com.lovbe.icharge.service.impl;

import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.exception.ErrorCode;
import com.lovbe.icharge.common.exception.ServiceErrorCodeConstants;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.entity.dto.AuthCodeReqDTO;
import com.lovbe.icharge.enums.CodeSceneEnum;
import com.lovbe.icharge.service.AuthCodeService;
import com.lovbe.icharge.util.RedisKeyConstant;
import com.lovbe.icharge.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
@Service
public class AuthCodeServiceImpl implements AuthCodeService {
    @Override
    public void sendMobileCode(AuthCodeReqDTO reqDTO) {

    }

    @Override
    public void sendEmailCode(AuthCodeReqDTO reqDTO) {

    }

    @Override
    public void useVerifyCode(AuthCodeReqDTO reqDTO) throws ServiceException {
        String key = RedisKeyConstant.getVerifyCode(reqDTO.getScene(), reqDTO.getMobile(), reqDTO.getEmail());
        // 查询redis
        String code = (String) RedisUtil.get(key);
        if (!StringUtils.hasLength(code)) {
            String incrKey = RedisKeyConstant.getVerifyCount(reqDTO.getScene(), reqDTO.getMobile(), reqDTO.getEmail());
            RedisUtil.incr(incrKey, 1);
            throw new ServiceException(ServiceErrorCodeConstants.AUTH_CODE_EXPIRED);
        }

        if (!Objects.equals(reqDTO.getCode(), code)) {
            String incrKey = RedisKeyConstant.getVerifyCount(reqDTO.getScene(), reqDTO.getMobile(), reqDTO.getEmail());
            RedisUtil.incr(incrKey, 1);
            saveErrorLog(reqDTO, ServiceErrorCodeConstants.AUTH_CODE_ERROR);
            throw new ServiceException(ServiceErrorCodeConstants.AUTH_CODE_ERROR);
        }
        // TODO 记录日志
    }

    private void saveErrorLog(AuthCodeReqDTO reqDTO, ErrorCode authCodeError) {
        // TODO 记录日志
        log.error("[验证码错误]--loginInfo: {}", JSONUtil.toJsonStr(reqDTO));
    }

    @Override
    public void validateSmsCode(AuthCodeReqDTO reqDTO) {

    }
}
