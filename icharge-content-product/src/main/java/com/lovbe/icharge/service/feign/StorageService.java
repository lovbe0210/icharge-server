package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.common.model.dto.UrlUploadDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "storage", fallback = StorageServiceFallback.class)
public interface StorageService {

    @PostMapping(value = "/api/st/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseBean<String> upload(FileUploadDTO fileUploadDTO);

    @PostMapping("/api/st/upload/url")
    ResponseBean<String> upload(@RequestBody @Valid UrlUploadDTO uploadDTO);
}
