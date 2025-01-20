package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 13:46
 * @Description: 人际关系
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "s_relationship")
public class RelationshipDo {
    /**
     * 主键 避免超出长度 前端丢失精度
     */
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 用户-主
     */
    private Long userIdMaster;
    /**
     * 用户-从
     */
    private Long userIdSlave;
    /**
     * 主关注从 0否1是
     */
    private Integer masterWatchSlave;
    /**
     * 从关注主 0否1是
     */
    private Integer slaveWatchMaster;
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
