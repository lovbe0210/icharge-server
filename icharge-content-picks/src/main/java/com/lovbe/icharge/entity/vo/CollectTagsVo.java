package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/26 0:04
 * @Description: 收藏夹分类标签
 */
@Data
public class CollectTagsVo extends BaseEntity {
    /**
     * 分类标签
     */
    private String title;

    /**
     * 收藏数
     */
    private int collectCount;
}
