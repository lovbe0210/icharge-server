package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.vo.PublicArticleVo;
import com.lovbe.icharge.entity.vo.ColumnVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/15 11:12
 * @Description: 成长轨迹
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("p_growth_stats")
public class GrowthStatsDo extends BaseEntity {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 统计时间范围 1近一年 0历史
     */
    private int rangeType;
    /**
     * 创作天数
     */
    private int creationDays;
    /**
     * 创作字数
     */
    private int creationWords;
    /**
     * 内容更新
     */
    private int updateContents;
    /**
     * 收获点赞
     */
    private int harvestLikes;
    /**
     * 文章总数
     */
    private int articleTotal;
    /**
     * 专栏总数
     */
    private int columnTotal;
    /**
     * 随笔总数
     */
    private int essayTotal;
    /**
     * 字数最多的专栏
     */
    private Long mostWordsColumnId;
    /**
     * 字数最多的文章
     */
    private Long mostWordsArticleId;
    /**
     * 字数最多文章或专栏标题
     */
    @TableField(exist = false)
    private String mostWordsTitle;
    /**
     * 最多字数
     */
    @TableField(exist = false)
    private int mostWords;
    /**
     * 最多字数专栏的文章数
     */
    @TableField(exist = false)
    private Integer mostColumnArticle;
    /**
     * 公开文档数
     */
    private int publicArticles;
    /**
     * 阅读量
     */
    private int articleViews;
    /**
     * 文章收录精选次数
     */
    private int articleFeatures;
    /**
     * 收获点赞量
     */
    private int contentLikes;
    /**
     * 收获评论量
     */
    private int contentComments;
}
