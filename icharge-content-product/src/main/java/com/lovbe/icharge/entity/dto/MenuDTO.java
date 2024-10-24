package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/24 23:44
 * @Description: MS
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_menu")
public class MenuDTO extends BaseEntity {
    /**
     * 菜单code
     */
    private String menuCode;
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 菜单类型 1一级菜单 2二级菜单
     */
    private int type;
    /**
     * 父级菜单
     */
    private String parentCode;
    /**
     * 排序字段
     */
    private int sort;
}
