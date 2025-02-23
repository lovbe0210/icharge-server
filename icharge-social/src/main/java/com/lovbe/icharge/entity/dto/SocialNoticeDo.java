package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/22 15:21
 * @Description: 社交通知
 */
@Data
@Accessors(chain = true)
@TableName("s_notices")
public class SocialNoticeDo extends BaseEntity {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 通知类型 1评论 2回复 3点赞 4新增关注 0系统消息
     */
    private int noticeType;
    /**
     * 点赞或评论对象id, 其他为0
     */
    private Long targetId;
    /**
     * 动作人，如果是系统通知则为0
     */
    private Long actionUserId;
    /**
     * 评论id 当type=1和2时不为空
     */
    private Long commentId;
    /**
     * 回复id 当type=2时不为空，
     */
    private Long replyId;
    /**
     * 阅读状态 0未读 1已读
     */
    private int read_status;

}
