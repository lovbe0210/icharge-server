package com.lovbe.icharge.common.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/23 23:20
 * @Description: 目录节点
 */
@Data
@Accessors(chain = true)
public class DirNodeVo {
    /**
     * 节点uid
     */
    private Long uid;
    /**
     * 单节点文章短地址
     */
    private String uri;
    /**
     * 节点类型 1单节点文章 2分组
     */
    private int type;
    /**
     * 节点标题
     */
    private String title;
    /**
     * 摘要
     */
    private String summary;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 单节点文章修改时间
     */
    private Date updateTime;
}
