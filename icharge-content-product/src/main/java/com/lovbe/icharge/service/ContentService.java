package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.dto.UploadDTO;
import jakarta.validation.Valid;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/11 22:17
 * @Description: 内容公共接口
 */
public interface ContentService {
    /**
     * @description: 内容相关文件上传
     * @param: uploadDTO
     * @param: userId
     * @return: java.lang.String
     * @author: lovbe0210
     * @date: 2025/3/11 22:18
     */
    String uploadContentFile(UploadDTO uploadDTO, Long userId);

    /**
     * @description: 通过url上传文件
     * @param: uploadDTO
     * @param: userId
     * @return: java.lang.String
     * @author: lovbe0210
     * @date: 2025/3/11 22:36
     */
    String uploadContentUrl(UploadDTO uploadDTO, Long userId);

}
