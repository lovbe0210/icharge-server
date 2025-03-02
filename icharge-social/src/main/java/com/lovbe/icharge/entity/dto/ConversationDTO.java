package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/1 22:49
 * @Description: 会话请求参数
 */
@Data
public class ConversationDTO {
    /**
     * 会话用户id
     */
    @NotNull(message = "会话用户不得为空")
    private Long targetUserId;
}
