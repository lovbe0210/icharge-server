package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import com.lovbe.icharge.entity.dto.ReplyCommentDTO;
import com.lovbe.icharge.entity.dto.TargetCommentDTO;
import com.lovbe.icharge.entity.vo.ReplyCommentVo;
import com.lovbe.icharge.service.ContentSocialService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/7 8:54
 * @Description: 内容相关互动 社交 互动
 */
@Slf4j
@RestController
public class ContentSocialController {
    @Resource
    private ContentSocialService socialService;

    /**
     * description: 文章点赞/取消点赞
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/content/like")
    public ResponseBean markContent(@RequestBody @Valid BaseRequest<ContentLikeDTO> baseRequest,
                                    @RequestHeader("userId") Long userId) {
        socialService.contentLikeMark(baseRequest.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * @description: 判断是否点过赞
     * @param: BaseRequest<ContentLikeDTO>
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/20 0:21
     */
    @PostMapping("/iflike")
    public ResponseBean ifLike(@RequestBody @Valid BaseRequest<ContentLikeDTO> baseRequest,
                               @RequestHeader("userId") Long userId) {
        boolean likeFlag = socialService.ifLike(baseRequest.getData(), userId);
        return ResponseBean.ok(likeFlag);
    }

    /**
     * @description: 获取评论列表
     * @param: BaseRequest<TargetCommentDTO>
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/20 0:22
     */
    @PostMapping("/target/comments")
    public ResponseBean getCommentList(@RequestBody @Valid BaseRequest<TargetCommentDTO> baseRequest,
                                       @RequestHeader(value = "userId", required = false) Long userId) {
        return socialService.getCommentList(baseRequest, userId);
    }

    /**
     * @description: 获取楼中楼回复
     * @param: BaseRequest<TargetCommentDTO>
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/20 0:22
     */
    @PostMapping("/comment/replies")
    public ResponseBean getCommentReplyList(@RequestBody @Valid BaseRequest<TargetCommentDTO> baseRequest,
                                            @RequestHeader(value = "userId", required = false) Long userId) {
        Assert.notNull(baseRequest.getData().getCommentId(), "回复评论的id不得为空");
        List<ReplyCommentVo> commentReplyList = socialService.getCommentReplyList(baseRequest, userId);
        return ResponseBean.ok(commentReplyList);
    }

    /**
     * @description: 发表评论或回复
     * @param: BaseRequest<ReplyCommentDTO>
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/20 0:22
     */
    @PutMapping("/target/comment")
    public ResponseBean replyComment(@Validated ReplyCommentDTO replyCommentDTO,
                                     @RequestHeader("userId") Long userId) {
        MultipartFile contentImg = replyCommentDTO.getContentImgFile();
        if (contentImg != null && contentImg.getSize() > SysConstant.SIZE_10MB) {
            throw new ServiceException(ServiceErrorCodes.FILE_OUT_SIZE_10);
        }
        ReplyCommentVo replyCommentVo = socialService.replyComment(replyCommentDTO, userId);
        return ResponseBean.ok(replyCommentVo);
    }

    /**
     * @description: 删除评论或回复
     * @param: BaseRequest<ReplyCommentDTO>
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/20 0:22
     */
    @PostMapping("/comment/delete/{uid}")
    public ResponseBean deleteReplyComment(@PathVariable("uid") Long uid,
                                           @RequestHeader("userId") Long userId) {
        socialService.deleteReplyComment(uid, userId);
        return ResponseBean.ok();
    }
}
