package com.lovbe.icharge.web.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.lovbe.icharge.storage.config.OssStorageFactory;
import com.lovbe.icharge.storage.service.OssStorageService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class CommonController {

    @PostMapping("/test/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        String fileUrl;
        try {
            String idStr = IdUtil.getSnowflakeNextIdStr();
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            fileUrl = OssStorageFactory.getStorageService().upload(file.getInputStream(), "test/" + idStr + "." + suffix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileUrl;
    }

    @PostMapping("/test/getFile")
    public String getFile(@RequestParam("path") String path) {
        return OssStorageFactory.getStorageService().getPublicUrl(path);
    }
}
