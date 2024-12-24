package com.lovbe.icharge.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Administrator
 * @Date: 2024/10/8 16:32
 * @Description: MS
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlUploadDTO {
    /**
     * 上传的源文件
     */
    @NotNull(message = "文件地址不得为空")
    private String url;
    /**
     * 路径前缀
     */
    @NotBlank(message = "文件上传场景不得为空")
    private String pathPrefix;
}
