package com.lovbe.icharge.common.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/11 0:32
 * @Description: 文件上传
 */
@Data
public class UploadDTO {
    /**
     * 上传的源文件
     */
    private MultipartFile file;
    /**
     * 唯一id，未登录用户使用此值
     */
    private String uq;
    /**
     * 上传的源文件
     */
    private String url;
}
