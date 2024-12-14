package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/14 23:55
 * @Description: MS
 */
@FeignClient(name = "user", fallback = UserServiceFallback.class)
public interface UserService {
    /**
     * 批量获取用户信息
     * @param batchRequest mapKey:userIdList  mapValue: List<Long>
     * @return
     */
    @PostMapping("/api/user/ids")
    ResponseBean<List<UserInfoDo>> getUserInfoList(@RequestBody @Validated BaseRequest<Map> batchRequest);
}
