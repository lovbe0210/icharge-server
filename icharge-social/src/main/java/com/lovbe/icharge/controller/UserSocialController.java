package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.model.dto.RelationshipDo;
import com.lovbe.icharge.common.model.vo.RelationshipVo;
import com.lovbe.icharge.entity.dto.TargetFollowDTO;
import com.lovbe.icharge.service.UserSocialService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/20 15:25
 * @Description: 人员相关社交接口
 */
@RestController
public class UserSocialController {
    @Resource
    private UserSocialService socialService;

    /**
     * @description: 用户关注操作
     * @param: baseRequest
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/23 15:03
     */
    @PostMapping("/user/action/follow")
    public ResponseBean userActionFollow(@RequestBody @Valid BaseRequest<TargetFollowDTO> baseRequest,
                                         @RequestHeader("userId") Long userId) {
        socialService.userActionFollow(baseRequest, userId);
        return ResponseBean.ok();
    }

    /**
     * @description: 获取指定人员的关注记录
     * @param: targetUser
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.common.model.dto.RelationshipDo>
     * @author: lovbe0210
     * @date: 2025/1/22 1:05
     */
    @GetMapping("/user/follow/{targetUser}")
    public ResponseBean<RelationshipDo> getRelationship(@PathVariable("targetUser") Long targetUser,
                                                        @RequestHeader("userId") Long userId) {
        return ResponseBean.ok(socialService.getRelationship(targetUser, userId));
    }

    /**
     * @description: 获取当前用户和指定人员的关注记录
     * @param: targetUser
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.common.model.dto.RelationshipDo>
     * @author: lovbe0210
     * @date: 2025/1/22 1:05
     */
    @PostMapping("/user/follows/query")
    public ResponseBean<List<RelationshipVo>> getRelationshipList(@RequestBody @Valid BaseRequest<List<Long>> baseRequest,
                                                                  @RequestHeader("userId") Long userId) {
        Assert.notEmpty(baseRequest.getData(), "查询用户id不得为空");
        return ResponseBean.ok(socialService.getRelationshipList(baseRequest.getData(), userId));
    }

    /**
     * @description: 获取关注或粉丝列表
     * @param: userId
     * @param: targetShip
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<java.util.List<com.lovbe.icharge.common.model.vo.RelationshipVo>>
     * @author: lovbe0210
     * @date: 2025/1/22 1:06
     */
    @PostMapping("/user/relationship/{ship}")
    public ResponseBean<PageBean> getFollowFansList(@RequestBody @Valid BaseRequest<RequestListDTO> baseRequest,
                                                    @PathVariable("ship") String targetShip,
                                                    @RequestHeader("userId") Long userId) {
        return ResponseBean.ok(socialService.getFollowFansList(userId, baseRequest.getData(), targetShip));
    }

    /**
     * @description: 获取所有关注用户id
     * @param: userId
     * @param: targetShip
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<java.util.List<com.lovbe.icharge.common.model.vo.RelationshipVo>>
     * @author: lovbe0210
     * @date: 2025/1/22 1:06
     */
    @PostMapping("/user/follows/users")
    public ResponseBean<List<Long>> getFollowUserList(@RequestHeader("userId") Long userId) {
        return ResponseBean.ok(socialService.getFollowUserList(userId));
    }

    /**
     * @description: 获取粉丝关注统计数
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<java.util.Map>
     * @author: lovbe0210
     * @date: 2025/3/13 0:19
     */
    @GetMapping("/user/relationship/count/{userId}")
    public ResponseBean<Map> getRelationShipStatistic(@PathVariable("userId") Long userId) {
        TargetStatisticDo statistic = socialService.getRelationShipStatistic(userId);
        Map<String, Integer> shipMap = Map.of(SysConstant.RELATIONSHIP_FOLLOW, statistic.getFollowCount(), SysConstant.RELATIONSHIP_FANS, statistic.getFansCount());
        return ResponseBean.ok(shipMap);
    }
}
