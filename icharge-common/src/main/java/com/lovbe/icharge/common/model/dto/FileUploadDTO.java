package com.lovbe.icharge.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Administrator
 * @Date: 2024/10/8 16:32
 * @Description: MS
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadDTO {
    /**
     * 上传的源文件
     */
    @NotNull(message = "文件内容不得为空")
    private MultipartFile file;
    /**
     * 路径前缀
     */
    @NotBlank(message = "文件上传场景不得为空")
    private String pathPrefix;
    /**
     * 路径所属（人员id或浏览器指纹）
     */
    @NotNull(message = "文件归属路径不得为空")
    private String pathFlag;
}
