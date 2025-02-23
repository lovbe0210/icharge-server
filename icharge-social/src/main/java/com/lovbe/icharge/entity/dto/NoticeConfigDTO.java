package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.Max;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/23 16:21
 * @Description: MS
 */
@Data
public class NoticeConfigDTO {
    /**
     * 新消息红点提醒
     */
    @Max(value = 0, message = "消息设置配置项错误")
    @Max(value = 1, message = "消息设置配置项错误")
    private Integer newMsgDot;
    /**
     * 新消息展示数量统计
     */
    @Max(value = 0, message = "消息设置配置项错误")
    @Max(value = 1, message = "消息设置配置项错误")
    private Integer msgCount;
    /**
     * 评论回复提醒人员范围 1 所有人 0关注的人 -1不接受任何提醒
     */
    @Max(value = -1, message = "消息设置配置项错误")
    @Max(value = 1, message = "消息设置配置项错误")
    private Integer commentMsgAccept;
    /**
     * 点赞消息提薪人员范围 1 所有人 0关注的人 -1不接受任何提醒
     */
    @Max(value = -1, message = "消息设置配置项错误")
    @Max(value = 1, message = "消息设置配置项错误")
    private Integer likeMsgAccept;
    /**
     * 他人关注是否提醒 0否1是
     */
    @Max(value = 0, message = "消息设置配置项错误")
    @Max(value = 1, message = "消息设置配置项错误")
    private Integer newFollowerMsg;
    /**
     * 是否开启系统通知 0否1是
     */
    @Max(value = 0, message = "消息设置配置项错误")
    @Max(value = 1, message = "消息设置配置项错误")
    private Integer systemNotice;
    /**
     * 是否开启私聊消息 0否1是
     */
    @Max(value = 0, message = "消息设置配置项错误")
    @Max(value = 1, message = "消息设置配置项错误")
    private Integer enableChatMessage;
}
