package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.RelationshipDo;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.common.model.vo.RelationshipVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * @description: 获取关注或粉丝列表
     * @param: userId
     * @param: targetShip
     * @return: ResponseBean<RelationshipVo>>
     * @author: lovbe0210
     * @date: 2025/1/22 15:13
     */
    @GetMapping("/api/sl/user/follows/query")
    ResponseBean<List<RelationshipVo>> getRelationshipList(@RequestBody List<Long> userIds,
                                                           @RequestHeader("userId") Long userId);
}
