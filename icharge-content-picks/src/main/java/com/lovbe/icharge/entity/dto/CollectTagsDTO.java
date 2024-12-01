package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/26 0:01
 * @Description: 收藏夹分类标签
 */
@Data
@Accessors(chain = true)
@TableName("p_collect_tags")
public class CollectTagsDTO extends BaseEntity {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 标题
     */
    @Length(max = 30, message = "收藏夹分类标签不得超过30字")
    private String title;

    /**
     * 收藏数
     */
    @TableField(exist = false)
    private int collectCount;
}
