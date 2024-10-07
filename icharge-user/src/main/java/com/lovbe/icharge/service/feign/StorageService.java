package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "storage")
public interface StorageService {

    @PostMapping("/api/storage/upload")
    ResponseBean<String> upload(MultipartFile file, String pathPrefix);
}
