package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.vo.PublicArticleVo;
import com.lovbe.icharge.common.model.vo.RamblyJotVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/24 13:13
 * @Description: 点赞消息通知
 */
@Data
@Accessors(chain = true)
public class LikeNoticeVo {
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
     * 点赞对象类型 1文章 2专栏 3随笔 4评论/回复
     */
    private Integer targetType;
    /**
     * 文章信息
     */
    private PublicArticleVo articleInfo;
    /**
     * 随笔信息
     */
    private RamblyJotVo ramblyJot;
    /**
     * 评论id
     */
    private Long commentId;
    /**
     * 评论内容
     */
    private String commentContent;
    /**
     * 回复id
     */
    private Long replyId;
    /**
     * 回复内容
     */
    private String replyContent;
    /**
     * 创建时间
     */
    private Date createTime;
}
