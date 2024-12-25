package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/25 20:11
 * @Description: 历史记录
 */
@Data
@Accessors(chain = true)
@TableName("p_browse_history")
public class BrowseHistoryDo {
    /**
     * targetId + 日期
     */
    @TableId
    private String uid;
    /**
     * 历史日期
     */
    private Date historyDate;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 记录id
     */
    private Long targetId;
    /**
     * 记录类型 1文章 2专栏 3随笔
     */
    private String targetType;
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
