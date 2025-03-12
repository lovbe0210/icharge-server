package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.dto.RamblyJotDo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/12 23:57
 * @Description: 用户创作记录
 */
@Accessors(chain = true)
@Data
public class CreateRecordVo {
    /**
     * 文章、专栏和随笔id
     */
    private Long targetId;
    /**
     * 类型 1文章 2专栏 3随笔
     */
    private int targetType;
    /**
     * 用户信息
     */
    private UserInfoDo userInfo;
    /**
     * 文章信息
     */
    private FeaturedArticleVo articleInfo;
    /**
     * 专栏信息
     */
    private RecommendColumnVo columnInfo;
    /**
     *
     */
    private RamblyJotDo ramblyJotDo;

    /**
     * 记录时间
     */
    private Date createTime;
    /**
     * 是否点赞
     */
    private int ifLike;
}
