package com.lovbe.icharge.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/21 17:33
 * @Description: 人机关系
 */
@Data
@Accessors(chain = true)
public class RelationshipVo {
    /**
     * 主键
     */
    private String uid;
    /**
     * 关注或粉丝uid
     */
    private Long userId;
    /**
     * 是否互相关注 0否1是
     */
    private int isEachFollow;
    /**
     * 更新时间
     **/
    private Date updateTime;
}
