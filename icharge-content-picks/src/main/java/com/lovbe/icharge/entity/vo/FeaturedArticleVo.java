package com.lovbe.icharge.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.util.JsonUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 1:32
 * @Description: 精选文章信息
 */
@Accessors(chain = true)
@Data
public class FeaturedArticleVo extends BaseEntity {
    /**
     * 文章标题
     */
    private String title;
    private String highLightTitle;
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
    private String columnName;
    /**
     * 文章摘要
     */
    private String summary;
    private String highLightSummary;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 文章标签
     */
    private List<Map> tags = new ArrayList<>();
    @JsonIgnore
    private String tagsStr;
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
     * 是否点赞
     */
    private boolean ifLike;
    /**
     * 是否收藏
     */
    private boolean ifCollect;

    public List<Map> getTags() {
        if (StringUtils.hasLength(tagsStr)) {
            try {
                List<Map> tagList = JsonUtils.parseArray(tagsStr, Map.class);
                return tagList;
            } catch (Exception e) {
                return tags;
            }
        }
        return tags;
    }
}
