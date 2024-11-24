package com.lovbe.icharge.entity;

import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/23 23:09
 * @Description: 公开专栏内容
 */
@Data
public class PublicColumnVo extends BaseEntity {
    /**
     * 专栏标题
     */
    private String title;
    /**
     * 展示路径
     */
    private String uri;
    /**
     * 所属用户id
     */
    private Long userId;
    /**
     * 目录内容id
     * <DirNodeVo>
     */
    private List dirContent;
    /**
     * 是否公开访问 0否1是
     */
    private int isPublic;
    /**
     * 是否开启评论功能 0否1是
     */
    private int enableComment;
}
