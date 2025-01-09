package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "p_user", autoResultMap = true)
public class CacheUserDo extends BaseEntity {
    /**
     * 用户名/昵称
     */
    private String username;
    /**
     * 用户等级
     */
    private Integer level;
    /**
     * 主页路径
     */
    private String domain;
    /**
     * 头像路径
     */
    private String avatarUrl;
    /**
     * 标签
     */
    private String tagsStr;
    /**
     * 简介
     */
    private String introduction;
    /**
     * 位置
     */
    private String location;
    /**
     * 行业
     */
    private String industry;
    /**
     * 主页自定义内容id
     */
    private String contentId;
    /**
     * 成长值
     */
    private Integer growthValue;
}
