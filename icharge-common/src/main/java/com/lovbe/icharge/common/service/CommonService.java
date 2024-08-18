package com.lovbe.icharge.common.service;

import com.lovbe.icharge.common.model.base.ResponseBean;

/**
 * @Author: lovbe
 * @Date: 2024/8/18 21:16
 * @Description: 公共接口
 */
public interface CommonService {

    /**
     * @description 根据滑块内容获取滑动验证码中的code值
     * @param[1] null
     * @author lovbe0210
     * @date 2024/8/18 22:10
     */
    ResponseBean getSliderVerifyCode(String sliderVerifyContent, String payload, String actionDesc);
}
