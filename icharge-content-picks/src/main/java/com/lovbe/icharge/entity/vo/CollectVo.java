package com.lovbe.icharge.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Set;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/1 15:14
 * @Description: MS
 */
@Data
@Accessors(chain = true)
public class CollectVo {
    private Long uid;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 个人主页地址
     */
    private String domain;
    /**
     * 用户名
     */
    private String username;
    /**
     * 目标id（文章或专栏id）
     */
    private Long targetId;
    /**
     * 文章地址
     */
    private String articleUri;
    /**
     * 收藏标题
     */
    private String name;
    /**
     * 专栏地址
     */
    private String columnUri;
    /**
     * 专栏名称
     */
    private String columnName;
    /**
     * 收藏类型 1文章 2专栏
     */
    private Integer targetType;
    /**
     * 收藏标签分类
     */
    private Set<Long> tags;
    /**
     * 数据状态
     */
    private String status;

    /**
     * 创建时间
     **/
    private Date createTime;

    /**
     * 更新时间
     **/
    private Date updateTime;
}
