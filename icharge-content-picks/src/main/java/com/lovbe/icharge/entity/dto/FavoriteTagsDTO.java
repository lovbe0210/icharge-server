package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/26 0:01
 * @Description: 收藏夹分类标签
 */
@Data
@TableName("p_collect_tags")
public class FavoriteTagsDTO extends BaseEntity {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 标题
     */
    @Length(max = 30, message = "收藏夹分类标签不得超过30字")
    private String title;
}
