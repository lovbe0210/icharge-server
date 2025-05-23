package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.vo.RelationshipVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/14 23:55
 * @Description: MS
 */
@FeignClient(name = "social", fallback = SocialServiceFallback.class)
public interface SocialService {
    /**
     * @description: 获取当前用户和指定人员的关注记录
     * @param: userId
     * @param: targetShip
     * @return: ResponseBean<RelationshipVo>>
     * @author: lovbe0210
     * @date: 2025/1/22 15:13
     */
    @PostMapping("/api/sl/user/follows/query")
    ResponseBean<List<RelationshipVo>> getRelationshipList(@RequestBody BaseRequest<List<Long>>  userIds,
                                                           @RequestHeader("userId") Long userId);

    /**
     * @description: 获取所有关注用户id
     * @param: userId
     * @param: targetShip
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<java.util.List<com.lovbe.icharge.common.model.vo.RelationshipVo>>
     * @author: lovbe0210
     * @date: 2025/1/22 1:06
     */
    @PostMapping("/api/sl/user/follows/users")
    ResponseBean<List<Long>> getFollowUserList(@RequestHeader("userId") Long userId);
}
