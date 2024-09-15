package com.lovbe.icharge.common.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.service.LoggingService;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: lovbe
 * @Date: 2024/8/18 21:16
 * @Description: MS
 */
@Slf4j
@Service
public class CommonServiceImpl implements CommonService {
    @Resource
    private LoggingService loggingService;

    /**
     * @description: 滑块验证失败的处理
     * @param: String  data.getCodeScene().getDescription() + "获取验证码"  LoginResultEnum.SLIDER_VERIFY_FAILED
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/8/18 15:34
     */
    private ResponseBean sliderVerifyFailed(String key, String payload, String actionDesc, String actionResult) {
        long sliderVerifyCode = YitIdHelper.nextId();
        RedisUtil.set(key, sliderVerifyCode, RedisKeyConstant.EXPIRE_2_HOUR);
        loggingService.recordLoginLog(null, payload, actionDesc, actionResult);
        return ResponseBean.error(GlobalErrorCodes.SLIDER_VERIFY_FAILED, String.valueOf(sliderVerifyCode));
    }
}
