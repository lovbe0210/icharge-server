package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "storage", fallback = StorageServiceFallback.class)
public interface StorageService {

    @PostMapping(value = "/api/storage/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseBean<String> upload(FileUploadDTO fileUploadDTO);
}
