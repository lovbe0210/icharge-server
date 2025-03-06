package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.entity.dto.MessageConfirmVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/28 14:22
 * @Description: MS
 */
@Data
@Accessors(chain = true)
public class MessageSessionVo {
    /**
     * 会话id
     */
    private Long uid;
    /**
     * 所属用户id
     */
    private Long ownerUserId;
    /**
     * 会话用户信息
     */
    private UserInfoDo sessionUserInfo;
    /**
     * 最后一次会话时间
     */
    private Date sessionTime;
    /**
     * 未读消息条数
     */
    private Integer unreadCount;
    /**
     * 是否单向会话会话 0否1是 对方主动回复或关注你前，最多发送1条消息
     */
    private Integer isOneWay;
    /**
     * 是否开启免打扰 0否1是
     */
    private Integer isNotDisturb;
    /**
     * 是否置顶 0否1是
     */
    private Integer isPinned;
    /**
     * 是否屏蔽会话用户 0否1是
     */
    private Integer isShield;
    /**
     * 最后一条消息
     */
    private MessageConfirmVo lastMsg;
}
