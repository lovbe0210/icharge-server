package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 23:59
 * @Description: MS
 */
@Data
@TableName("s_comment")
public class ReplyCommentDo extends BaseEntity {
    /**
     * 评论对象id
     */
    private Long targetId;
    /**
     * 父级评论id
     */
    private Long parentId;
    /**
     * 评论用户id
     */
    private Long userId;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论图片地址
     */
    private String contentImgUrl;
    /**
     * 评论点赞数
     */
    private int likeCount;
    /**
     * 楼中楼回复@用户id
     */
    private Long replyUserId;
    /**
     * 评论提交ip
     */
    private String submitIp;
    /**
     * 评论提交城市
     */
    private String submitCity;
}
