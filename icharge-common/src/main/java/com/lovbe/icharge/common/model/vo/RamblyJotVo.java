package com.lovbe.icharge.common.model.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:56
 * @Description: 随笔
 */
@Accessors(chain = true)
@Data
public class RamblyJotVo extends BaseEntity {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 预览内容
     */
    private String previewContent;
    /**
     * 所属用户
     */
    private UserInfoDo userInfo;
    /**
     * 封面地址
     */
    private List<String> previewImg;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 评论数
     */
    private Integer commentCount;
    /**
     * 是否公开访问 0否1是
     */
    private int isPublic;
    /**
     * 发布状态 1审核中 2审核失败 3已发布
     */
    private int publishStatus;
    /**
     * 我已点赞,需要关联查库
     */
    private boolean ifLike;
    /**
     * 点赞用户列表
     */
    private List<UserInfoDo> likeUserList;
}
