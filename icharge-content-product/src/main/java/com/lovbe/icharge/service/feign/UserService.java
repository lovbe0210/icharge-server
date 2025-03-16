package com.lovbe.icharge.service.feign;

import cn.hutool.db.Page;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user", fallback = UserServiceFallback.class)
public interface UserService {

    /**
     * @description: 通过userId批量获取用户信息
     * @param: BaseRequest<BatchUserRequestDTO>
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:39
     */
    @PostMapping("/api/user/ids")
    ResponseBean<PageBean<UserInfoDo>> getUserInfoList(@RequestBody @Valid BaseRequest<Page> batchRequest);
}
