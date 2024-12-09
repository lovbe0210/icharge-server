package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "social", fallback = SocialServiceFallback.class)
public interface SocialService {

    @PostMapping(value = "/api/sl/iflike")
    ResponseBean<Boolean> iflike(BaseRequest<ContentLikeDTO> request, @RequestHeader("userId") Long userId);
}
