package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.vo.RelationshipVo;
import com.lovbe.icharge.entity.dto.RelationshipDo;
import com.lovbe.icharge.entity.dto.TargetFollowDTO;
import com.lovbe.icharge.service.UserSocialService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/20 15:25
 * @Description: 人员相关社交接口
 */
@RestController
public class UserSocialController {
    @Resource
    private UserSocialService socialService;

    @PostMapping("/user/action/follow")
    public ResponseBean userActionFollow(@RequestBody @Valid BaseRequest<TargetFollowDTO> baseRequest,
                                         @RequestHeader("userId") Long userId) {
        socialService.userActionFollow(baseRequest, userId);
        return ResponseBean.ok();
    }

    /**
     * @description: 获取关注记录
     * @param: targetUser
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.entity.dto.RelationshipDo>
     * @author: lovbe0210
     * @date: 2025/1/22 1:05
     */
    @GetMapping("/user/follow/{targetUser}")
    public ResponseBean<RelationshipDo> getRelationship(@PathVariable("targetUser") Long targetUser,
                                                        @RequestHeader("userId") Long userId) {
        return ResponseBean.ok(socialService.getRelationship(targetUser, userId));
    }

    /**
     * @description: 获取关注或粉丝列表
     * @param: userId
     * @param: targetShip
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<java.util.List<com.lovbe.icharge.entity.vo.RelationshipVo>>
     * @author: lovbe0210
     * @date: 2025/1/22 1:06
     */
    @GetMapping("/user/relationship/{ship}")
    public ResponseBean<List<RelationshipVo>> getRelationshipList(@RequestHeader("userId") Long userId,
                                                                  @PathVariable("ship") String targetShip) {
        return ResponseBean.ok(socialService.getRelationshipList(userId, targetShip));
    }
}
