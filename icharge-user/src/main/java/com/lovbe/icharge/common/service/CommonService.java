package com.lovbe.icharge.common.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @description: 公共类
 * @author: Lvhl
 * @date: 2024/5/10 19:47
 */
public interface CommonService {
    /**
     * description: 文件上传，获取可访问的地址
     * @author: Lvhl
     * @date: 2024/5/10 19:49
     * @param file
     * @return String
     */
    String upload(MultipartFile file);
}
