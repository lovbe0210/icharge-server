package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.entity.dto.NoticeConfigDTO;
import com.lovbe.icharge.entity.dto.NoticeConfigDo;
import com.lovbe.icharge.entity.dto.SocialNoticeReqDTO;
import com.lovbe.icharge.entity.vo.CommentNoticeVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/23 16:09
 * @Description: 消息通知服务
 */
public interface MessageNoticeService {
    /**
     * @description: 获取通知项设置
     * @param: userId
     * @return: com.lovbe.icharge.entity.dto.NoticeConfigDo
     * @author: lovbe0210
     * @date: 2025/2/23 16:11
     */
    NoticeConfigDo getNoticeSetting(Long userId);

    /**
     * @description: 消息设置项修改
     * @param: data
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/2/23 16:24
     */
    void updateNoticeSetting(NoticeConfigDTO data, Long userId);

    /**
     * @description: 获取评论回复通知
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.PageBean<com.lovbe.icharge.entity.vo.CommentNoticeVo>
     * @author: lovbe0210
     * @date: 2025/2/24 15:12
     */
    PageBean<CommentNoticeVo> getCommentNotice(SocialNoticeReqDTO data, Long userId);
}
