package com.lovbe.icharge.entity.dto;

import com.lovbe.icharge.common.enums.LoginLogTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/3 13:01
 * @Description: MS
 */
@Data
public class OAuthLoginDTO {
    /**
     * 登录方式
     */
    @NotNull(message = "登录方式不得为空")
    private LoginLogTypeEnum loginType;
}
