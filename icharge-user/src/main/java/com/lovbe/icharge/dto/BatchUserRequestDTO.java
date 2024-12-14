package com.lovbe.icharge.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/14 23:43
 * @Description: 批量获取用户信息
 */
@Data
public class BatchUserRequestDTO {
    @NotEmpty(message = "用户id不得为空")
    private List<Long> userIdList;
}
