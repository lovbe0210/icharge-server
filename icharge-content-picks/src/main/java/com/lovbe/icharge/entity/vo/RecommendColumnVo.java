package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/29 22:36
 * @Description: 推荐专栏信息
 */
@Data
public class RecommendColumnVo extends BaseEntity {
    /**
     * 专栏标题
     */
    private String title;
    /**
     * 展示路径
     */
    private String uri;
    /**
     * 专栏简介
     */
    private String synopsis;
    /**
     * 所属用户信息
     */
    private UserInfoDo userInfo;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 文章数
     */
    private int articleCount;
    /**
     * 点赞数
     */
    private int likeCount;
    /**
     * 浏览量
     */
    private int viewCount;
    /**
     * 评论数
     */
    private int commentCount;
    /**
     * 收藏数
     */
    private int collectCount;
    /**
     * 是否收藏
     */
    private boolean ifCollect;
}
