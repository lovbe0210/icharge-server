package com.lovbe.icharge.common.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.storage.config.OssStorageFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class CommonServiceImpl implements CommonService {
    @Override
    public String upload(MultipartFile file) {
        String fileUrl;
        try {
            String idStr = IdUtil.getSnowflakeNextIdStr();
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            fileUrl = OssStorageFactory.getStorageService().upload(file.getInputStream(), "web/theme/" + idStr + "." + suffix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return OssStorageFactory.getStorageService().getPublicUrl(fileUrl);
    }
}
