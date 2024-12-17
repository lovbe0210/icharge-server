package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import com.lovbe.icharge.entity.dto.TargetCommentDTO;

/**
 * @description: Oss基础客户端
 * @author: Lvhl
 * @date: 2024/5/9 13:20
 */
public interface ContentSocialService {
    /** 
     * @description: 点赞/取消点赞
     * @param: ContentLikeDTO
     * @return: Long
     * @author: lovbe0210
     * @date: 2024/12/5 0:38
     */
    void contentLikeMark(ContentLikeDTO data, Long userId);

    /**
     * @description: 判断是否已点赞
     * @param: ContentLikeDTO
     * @return: boolean
     * @author: lovbe0210
     * @date: 2024/12/9 23:45
     */
    boolean ifLike(ContentLikeDTO data, Long userId);

    /**
     * @description: 获取评论列表
     * @param: BaseRequest<TargetCommentDTO>
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/17 16:52
     */
    ResponseBean getCommentList(BaseRequest<TargetCommentDTO> baseRequest, Long userId);
}
