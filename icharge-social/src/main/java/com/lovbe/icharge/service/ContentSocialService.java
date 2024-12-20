package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import com.lovbe.icharge.entity.dto.ReplyCommentDTO;
import com.lovbe.icharge.entity.dto.TargetCommentDTO;
import com.lovbe.icharge.entity.vo.ReplyCommentVo;

import java.util.List;

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

    /**
     * @description: 发表评论或回复
     * @param: BaseRequest<ReplyCommentDTO>
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/20 0:23
     */
    ReplyCommentVo replyComment(ReplyCommentDTO replyCommentDTO, Long userId);

    /**
     * @description: 获取楼中楼回复
     * @param: BaseRequest<TargetCommentDTO>
     * @return: List<ReplyCommentVo>
     * @author: lovbe0210
     * @date: 2024/12/20 18:51
     */
    List<ReplyCommentVo> getCommentReplyList(BaseRequest<TargetCommentDTO> baseRequest, Long userId);
}
