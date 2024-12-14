package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UserServiceFallback extends BaseFallback implements UserService {

    @Override
    public ResponseBean<List<UserInfoDo>> getUserInfoList(BaseRequest<Map> batchRequest) {
        return getFallBack();
    }
}
