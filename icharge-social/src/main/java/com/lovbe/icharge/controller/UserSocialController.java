package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.RelationshipDo;
import com.lovbe.icharge.entity.dto.TargetCommentDTO;
import com.lovbe.icharge.entity.dto.TargetFollowDTO;
import com.lovbe.icharge.service.UserSocialService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/20 15:25
 * @Description: 人员相关社交接口
 */
@RestController
public class UserSocialController {
    @Resource
    private UserSocialService socialService;

    @PostMapping("/user/follow")
    public ResponseBean userActionFollow(@RequestBody @Valid BaseRequest<TargetFollowDTO> baseRequest,
                                         @RequestHeader("userId") Long userId) {
        socialService.userActionFollow(baseRequest, userId);
        return ResponseBean.ok();
    }

    @GetMapping("/user/relationship/{targetUser}")
    public ResponseBean<RelationshipDo> getRelationship(@PathVariable("targetUser") Long targetUser,
                                                        @RequestHeader("userId") Long userId) {
        return ResponseBean.ok(socialService.getRelationship(targetUser, userId));
    }
}
