package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.NoticeConfigDTO;
import com.lovbe.icharge.entity.dto.NoticeConfigDo;
import com.lovbe.icharge.entity.dto.SocialNoticeReqDTO;
import com.lovbe.icharge.entity.vo.CommentNoticeVo;
import com.lovbe.icharge.entity.vo.LikeNoticeVo;
import com.lovbe.icharge.entity.vo.SocialNoticeVo;
import com.lovbe.icharge.entity.vo.UnreadMsgStatisticVo;
import com.lovbe.icharge.service.ChatMessageService;
import com.lovbe.icharge.service.MessageNoticeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/23 15:33
 * @Description: 社交通知
 */
@RestController
public class SocialNoticeController {
    @Resource
    private MessageNoticeService messageNoticeService;
    @Resource
    private ChatMessageService chatMessageService;

    /**
     * @description: 获取为未读消息统计
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/23 15:36
     */
    @GetMapping("/notice/unread/statistic")
    public ResponseBean getUnreadStatistic(@RequestHeader(SysConstant.USERID) Long userId) {
        UnreadMsgStatisticVo unreadMsgStatistic = chatMessageService.getUnreadStatistic(userId);
        return ResponseBean.ok(unreadMsgStatistic);
    }

    /**
     * @description: 获取消息设置
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/23 15:36
     */
    @GetMapping("/notice/setting")
    public ResponseBean getNoticeSetting(@RequestHeader(SysConstant.USERID) Long userId) {
        NoticeConfigDo noticeConfig = messageNoticeService.getNoticeSetting(userId);
        return ResponseBean.ok(noticeConfig);
    }

    /**
     * @description: 消息设置改动
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/23 15:36
     */
    @PostMapping("/notice/setting/update")
    public ResponseBean updateNoticeSetting(@RequestBody @Valid BaseRequest<NoticeConfigDTO> baseRequest,
                                            @RequestHeader(SysConstant.USERID) Long userId) {
        messageNoticeService.updateNoticeSetting(baseRequest.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * @description: 获取评论回复通知
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/23 15:36
     */
    @PostMapping("/notice/comments")
    public ResponseBean getCommentNotice(@RequestBody @Valid BaseRequest<SocialNoticeReqDTO> baseRequest,
                                         @RequestHeader(SysConstant.USERID) Long userId) {
        PageBean<CommentNoticeVo> commentNotices = messageNoticeService.getCommentNotice(baseRequest.getData(), userId);
        return ResponseBean.ok(commentNotices);
    }

    /**
     * @description: 获取点赞消息通知
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/23 15:36
     */
    @PostMapping("/notice/likes")
    public ResponseBean getLikesNotice(@RequestBody @Valid BaseRequest<SocialNoticeReqDTO> baseRequest,
                                       @RequestHeader(SysConstant.USERID) Long userId) {
        PageBean<LikeNoticeVo> likeNotices = messageNoticeService.getLikesNotice(baseRequest.getData(), userId);
        return ResponseBean.ok(likeNotices);
    }

    /**
     * @description: 获取关注消息通知
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/23 15:36
     */
    @PostMapping("/notice/follows")
    public ResponseBean getFollowsNotice(@RequestBody @Valid BaseRequest<SocialNoticeReqDTO> baseRequest,
                                       @RequestHeader(SysConstant.USERID) Long userId) {
        PageBean<SocialNoticeVo> likeNotices = messageNoticeService.getFollowsNotice(baseRequest.getData(), userId);
        return ResponseBean.ok(likeNotices);
    }
}
