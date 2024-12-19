package com.lovbe.icharge.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 23:59
 * @Description: 评论回复
 */
@Data
@Accessors(chain = true)
public class ReplyCommentVo extends BaseEntity {
    /**
     * 父级评论id
     */
    private Long parentId;
    /**
     * 评论用户id
     */
    private Long userId;
    /**
     * 用户信息
     */
    private UserInfoDo userInfo;
    /**
     * 楼中楼回复用户信息
     */
    private UserInfoDo replyUserInfo;
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
     * 回复总条数
     */
    private int replyCount;
    /**
     * 评论提交ip
     */
    private String submitIp;
    /**
     * 评论提交城市
     */
    private String submitCity;
    /**
     * 是否置顶 0否1是
     */
    private int isTop;
    /**
     * 是否精选 0否1是
     */
    private int isFeature;
    /**
     * 是否点赞 0否1是
     */
    private int ifLike;
    /**
     * 楼中楼回复
     */
    private List<ReplyCommentVo> deepReplyList;
}
