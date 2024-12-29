package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 1:32
 * @Description: 推荐文章信息
 */
@Accessors(chain = true)
@Data
public class RecommendArticleVo extends BaseEntity {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 展示路径
     */
    private String uri;
    /**
     * 所属用户信息
     */
    private UserInfoDo userInfo;
    /**
     * 所属专栏
     */
    private Long columnId;
    /**
     * 所属专栏uri
     */
    private String columnUri;
    /**
     * 所属专栏标题
     */
    private String columnTitle;
    /**
     * 文章摘要
     */
    private String summary;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 文章标签
     */
    private List<Map> tags = new ArrayList<>();
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
     * 是否点赞
     */
    private boolean ifLike;
}
