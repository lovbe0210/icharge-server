package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.vo.SecServiceSvVo;

/**
 * @Author: lovbe0210
 * @Date: 2024/9/15 8:58
 * @Description: 页面埋点校验相关
 */
public interface SecurityService {
    
    /** 
     * @description 获取滑块验证码的埋点cookie
     * @param[1] scvo 
     * @return ResponseBean 
     * @author lovbe0210
     * @date 2024/9/15 9:00
     */
    ResponseBean setSliderValidCookie(SecServiceSvVo scvo);
}
