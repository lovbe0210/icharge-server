package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/8 13:28
 * @Description: 消息删除
 */
@Data
public class MessageActionDTO {
    @NotNull(message = "消息id不得为空")
    private Long messageId;
}
