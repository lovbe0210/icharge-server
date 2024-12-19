package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import org.springframework.stereotype.Component;

@Component
public class StorageServiceFallback extends BaseFallback implements StorageService {

    @Override
    public ResponseBean<String> upload(FileUploadDTO fileUploadDTO) {
        return getFallBack();
    }
}
