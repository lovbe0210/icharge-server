package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

//@Component
public class StorageServiceFeignFallback extends BaseFallback implements StorageService {

    @Override
    public ResponseBean<String> upload(MultipartFile file, String pathPrefix) {
        return getFallBack();
    }
}
