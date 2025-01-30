package com.lovbe.icharge.entity.dto;

import lombok.Data;

/**
 * @Author: Lvhl
 * @Date: 2024/9/16 11:36
 * @Description: 验证码发送结果
 */
@Data
public class SimpleSendResultDTO {
    /**
     * 发送结果
     */
    private boolean result;

    /**
     * 发送日志，当接口到达短信api时应该返回此值
     */
    private Long sendLogId;

    /**
     * 其他说明
     */
    private String mark;
}
