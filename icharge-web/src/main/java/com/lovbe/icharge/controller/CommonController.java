package com.lovbe.icharge.controller;

import com.lovbe.icharge.model.base.ResponseBean;
import com.lovbe.icharge.common.service.CommonService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
public class CommonController {
    @Resource
    CommonService commonService;

    /**
     * 简单文件上传
     * @param file
     * @return
     */
    @PostMapping("/common/upload")
    public ResponseBean upload(@RequestParam("file") MultipartFile file) {
        String publicUrl = commonService.upload(file);
        return ResponseBean.ok(publicUrl);
    }
}
