package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.validation.Mobile;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @description: 邮箱相关请求体
 * @author: Lvhl
 * @date: 2024/8/7 13:10
 */
@Data
public class AuthMobileVO {
    /**
     * 手机号
     */
    @Mobile
    @NotEmpty(message = "手机号不能为空")
    private String mobile;
}
