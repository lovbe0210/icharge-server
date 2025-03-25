package com.lovbe.icharge.common.model.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/29 22:36
 * @Description: 推荐专栏信息
 */
@Data
@Accessors(chain = true)
public class RecommendColumnVo extends BaseEntity {
    /**
     * 专栏标题
     */
    private String title;
    private String highLightTitle;
    /**
     * 展示路径
     */
    private String uri;
    /**
     * 专栏简介
     */
    private String synopsis;
    private String highLightSynopsis;
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
     * 专栏总字数
     */
    private int totalWords;
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
    /**
     * 首页自定义内容
     */
    private Long homeContentId;
    private String homeContent;
    /**
     * 收藏标签分类
     */
    private Set<Long> tags;
    /**
     * 收藏id
     */
    private Long collectId;
}
