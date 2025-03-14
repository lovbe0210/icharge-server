package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.vo.PublicArticleVo;
import com.lovbe.icharge.common.model.vo.RamblyJotVo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/27 22:20
 * @Description: 系统通知
 */
@Data
public class SystemNoticeVo {
    private Long uid;
    /**
     * 目前就是文章id
     */
    private Long targetId;
    /**
     * 通知类型 1文章 2专栏 3随笔
     */
    private int targetType;
    /**
     * 通知内容
     */
    private String content;
    /**
     * 通知url显示label
     */
    private String label;
    /**
     * 请求路径，可以是https://全路径也可以是/viw/asdas
     */
    private String url;
    /**
     * 文章信息
     */
    private PublicArticleVo articleInfo;
    /**
     * 随笔信息
     */
    private RamblyJotVo ramblyJot;
    /**
     * 是否已读
     */
    private int readStatus;
    /**
     * 创建时间
     */
    private Date createTime;
}
