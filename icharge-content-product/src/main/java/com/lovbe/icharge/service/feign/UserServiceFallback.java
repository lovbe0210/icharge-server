package com.lovbe.icharge.service.feign;

import cn.hutool.db.Page;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.PreferenceSettingVo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/11 22:32
 * @Description: MS
 */
@Component
public class UserServiceFallback extends BaseFallback implements UserService{
    @Override
    public ResponseBean<PageBean<UserInfoDo>> getUserInfoList(@RequestBody BaseRequest<Page> batchRequest) {
        return getFallBack();
    }
}
