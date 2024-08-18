package com.lovbe.icharge.common.service;

/**
 * @Author: lovbe
 * @Date: 2024/8/18 22:00
 * @Description: 日志接口
 */
public interface LoggingService {
    /**
     * @description 记录登录日志
     * @param[1] uid
     * @param[2] payload
     * @param[3] actionDesc
     * @param[4] actionResult
     * @author lovbe0210
     * @date 2024/8/18 22:04
     */
    void recordLoginLog(Long uid, String payload, String actionDesc, String actionResult);
}
