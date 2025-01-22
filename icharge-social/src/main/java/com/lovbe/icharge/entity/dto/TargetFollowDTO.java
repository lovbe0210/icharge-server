package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/20 17:17
 * @Description: 关注/取消关注
 */
@Data
public class TargetFollowDTO {
    /**
     * 操作人id
     */
    private Long userId;
    /**
     * 关注的用户id
     */
    @NotNull(message = "关注的用户不得为空")
    private Long targetUser;
    /**
     * 操作类型 0取消关注 1添加关注
     */
    @NotNull(message = "操作类型不得为空")
    @Max(value = 1, message = "操作类型错误")
    @Min(value = 0, message = "操作类型错误")
    private Integer action;
}
