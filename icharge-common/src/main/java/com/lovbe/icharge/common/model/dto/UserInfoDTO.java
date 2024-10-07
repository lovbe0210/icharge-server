package com.lovbe.icharge.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: Administrator
 * @Date: 2024/9/30 17:11
 * @Description: MS
 */
@Data
public class UserInfoDTO {
    /**
     * 用户id
     */
    @NotNull(message = "用户id不得为空")
    private Long userId;
}
