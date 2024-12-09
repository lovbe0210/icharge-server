package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import com.lovbe.icharge.entity.dto.ContentLikeDTO;
import org.springframework.stereotype.Component;

@Component
public class SocialServiceFallback extends BaseFallback implements SocialService {

    @Override
    public ResponseBean<Boolean> iflike(BaseRequest<ContentLikeDTO> request, Long userId) {
        return getFallBack();
    }
}
