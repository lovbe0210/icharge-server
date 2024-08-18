package com.lovbe.icharge.common.service.impl;

import com.lovbe.icharge.common.service.LoggingService;
import org.springframework.stereotype.Service;

/**
 * @Author: lovbe
 * @Date: 2024/8/18 22:05
 * @Description: MS
 */
@Service
public class LoggingServiceImpl implements LoggingService {
    @Override
    public void recordLoginLog(Long uid, String payload, String actionDesc, String loginResult) {

    }
}
