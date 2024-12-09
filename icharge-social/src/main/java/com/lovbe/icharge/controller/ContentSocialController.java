package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import com.lovbe.icharge.service.ContentSocialService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/iflike")
    public ResponseBean ifLike(@RequestBody @Valid BaseRequest<ContentLikeDTO> baseRequest,
                               @RequestHeader("userId") Long userId) {
        boolean likeFlag = socialService.ifLike(baseRequest.getData(), userId);
        return ResponseBean.ok(likeFlag);
    }
}
