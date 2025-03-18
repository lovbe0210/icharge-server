package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 13:46
 * @Description: MS
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "s_like_item")
public class LikeActionDo extends BaseEntity {
    /**
     * 点赞对象id
     */
    private Long targetId;
    /**
     * 点赞对象用户id
     */
    @TableField(exist = false)
    private Long targetUserId;

    /**
     * 点赞对象类型  1文章 2专栏 3随笔 4评论
     */
    private Integer targetType;
    /**
     * 点赞用户id
     */
    private Long userId;
    /**
     * 点赞动作 1点赞 0取消
     */
    @TableField(exist = false)
    private Integer action;
    /**
     * 标记位 是否为首次点赞
     */
    @TableField(exist = false)
    private boolean newAction;

    public LikeActionDo(Long targetId, Integer targetType, Long userId) {
        this.targetId = targetId;
        this.targetType = targetType;
        this.userId = userId;
    }

    public LikeActionDo(Long targetId, Long targetUserId, Integer targetType, Long userId, Integer action) {
        this.targetId = targetId;
        this.targetUserId = targetUserId;
        this.targetType = targetType;
        this.userId = userId;
        this.action = action;
    }
}
