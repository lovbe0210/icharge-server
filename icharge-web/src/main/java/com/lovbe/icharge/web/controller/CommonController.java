package com.lovbe.icharge.web.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.lovbe.icharge.common.model.ResponseBean;
import com.lovbe.icharge.storage.config.OssStorageFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class CommonController {

    @PostMapping("/common/upload")
    public ResponseBean upload(@RequestParam("file") MultipartFile file) {
        String fileUrl;
        try {
            String idStr = IdUtil.getSnowflakeNextIdStr();
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            fileUrl = OssStorageFactory.getStorageService().upload(file.getInputStream(), "web/theme/" + idStr + "." + suffix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String publicUrl = OssStorageFactory.getStorageService().getPublicUrl(fileUrl);
        return ResponseBean.ok(publicUrl);
    }
}
