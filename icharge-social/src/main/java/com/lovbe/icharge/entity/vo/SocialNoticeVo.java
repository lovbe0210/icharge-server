package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/27 22:20
 * @Description: 关注消息通知
 */
@Data
public class SocialNoticeVo {
    private Long uid;
    /**
     * 通知类型 1评论 2回复 3点赞 4新增关注 0系统消息
     */
    private int noticeType;
    /**
     * 动作发起人信息
     */
    private UserInfoDo actionUserInfo;
    /**
     * 已读状态 0否1是
     */
    private Integer readStatus;
    /**
     * 创建时间
     */
    private Date createTime;
}
