package com.lovbe.icharge.controller;

import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.config.OssStorageFactory;
import com.lovbe.icharge.service.OssStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/7 8:54
 * @Description: 文件存储服务
 */
@Slf4j
@RestController
public class OssStorageController {

    @PostMapping("/upload")
    public ResponseBean upload(@Validated FileUploadDTO fileUploadDTO) {
        OssStorageService storageService = OssStorageFactory.getStorageService();
        String originalFilename = fileUploadDTO.getFile().getOriginalFilename();
        String path = fileUploadDTO.getPathPrefix() + "/" + YitIdHelper.nextId() + "_" + originalFilename;
        try {
            String upload = storageService.upload(fileUploadDTO.getFile().getInputStream(), path);
            return ResponseBean.ok(upload);
        } catch (IOException e) {
            log.error("[文件上传] -- 上传失败，errorInfo: {}", e.toString());
            return ResponseBean.error(ServiceErrorCodes.FILE_UPLOAD_FAILED, null);
        }
    }
}
