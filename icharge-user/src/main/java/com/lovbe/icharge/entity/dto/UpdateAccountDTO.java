package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/1 18:54
 * @Description: 账号信息修改
 */
@Data
public class UpdateAccountDTO {
    /**
     * 账号信息修改场景
     */
    @NotNull(message = "账号信息修改场景不得为空")
    private Integer scene;
    /**
     * 更新账号签名
     */
    @NotNull(message = "更新账号信息不得为空")
    private String sign;
}
