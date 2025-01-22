package com.lovbe.icharge.common.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/21 17:33
 * @Description: 人机关系
 */
@Data
@Accessors(chain = true)
public class RelationshipVo {
    /**
     * 关注或粉丝用户id
     */
    private Long uid;
    /**
     * 关注或粉丝记录id
     */
    private String followActionId;
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
    private List<Map> tags = new ArrayList<>();
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
     * 是否互相关注 0否1是
     */
    private int isEachFollow;
    /**
     * 更新时间
     **/
    private Date updateTime;
    /**
     * 数据状态
     */
    private String status;
}
