package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/22 15:21
 * @Description: 消息通知设置
 */
@Data
@Accessors(chain = true)
@TableName("s_notice_config")
@NoArgsConstructor
@AllArgsConstructor
public class NoticeConfigDo extends BaseEntity {
    /**
     * 新消息红点提醒
     */
    private int newMsgDot;
    /**
     * 新消息展示数量统计
     */
    private int msgCount;
    /**
     * 评论回复提醒接收范围 1 所有人 0关注的人 -1不接受任何提醒
     */
    private int commentMsgAccept;
    /**
     * 点赞消息提薪接收范围 1 所有人 0关注的人 -1不接受任何提醒
     */
    private int likeMsgAccept;
    /**
     * 他人关注是否提醒 0否1是
     */
    private int newFollowerMsg;
    /**
     * 是否开启系统通知 0否1是
     */
    private int systemNotice;
    /**
     * 是否开启私聊消息 0否1是
     */
    private int enableChatMessage;

    public NoticeConfigDo(boolean init) {
        if (init) {
            this.newMsgDot = 1;
            this.msgCount = 1;
            this.commentMsgAccept = 1;
            this.likeMsgAccept = 1;
            this.newFollowerMsg = 1;
            systemNotice = 1;
            enableChatMessage = 1;
            this.setStatus(CommonStatusEnum.NORMAL.getStatus());
            this.setUpdateTime(new Date());
            this.setCreateTime(new Date());
        }
    }
}
