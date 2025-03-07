package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/7 22:09
 * @Description: MS
 */
@Data
public class ConversationUpdateDTO {
    /**
     * 会话id
     */
    @NotNull(message = "会话id不得为空")
    private Long sessionId;
    /**
     * 是否开启免打扰 0否1是
     */
    @Max(value = 1, message = "会话设置参数错误")
    @Min(value = 0, message = "会话设置参数错误")
    private Integer isNotDisturb;
    /**
     * 是否置顶 0否1是
     */
    @Max(value = 1, message = "会话设置参数错误")
    @Min(value = 0, message = "会话设置参数错误")
    private Integer isPinned;
    /**
     * 是否屏蔽会话用户 0否1是
     */
    @Max(value = 1, message = "会话设置参数错误")
    @Min(value = 0, message = "会话设置参数错误")
    private Integer isShield;
    /**
     * 未读数
     */
    @Min(value = 0, message = "会话设置参数错误")
    private Integer unreadCount;
}
