package com.lovbe.icharge.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.common.model.dto.UrlUploadDTO;
import com.lovbe.icharge.config.OssStorageFactory;
import com.lovbe.icharge.service.OssStorageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
        if (StringUtils.hasText(originalFilename)) {
            originalFilename = StrUtil.trim(originalFilename);
            originalFilename = StrUtil.cleanBlank(originalFilename).replaceAll("\\s+", "");
        }
        String path = fileUploadDTO.getPathFlag() + "/" + fileUploadDTO.getPathPrefix() + "/" + YitIdHelper.nextId() + SysConstant.SEPARATOR + originalFilename;
        try {
            String upload = storageService.upload(fileUploadDTO.getFile().getInputStream(), path);
            return ResponseBean.ok(upload);
        } catch (IOException e) {
            log.error("[文件上传] -- 上传失败，errorInfo: {}", e.toString());
            return ResponseBean.error(ServiceErrorCodes.FILE_UPLOAD_FAILED, null);
        }
    }

    @PostMapping("/upload/url")
    public ResponseBean upload(@RequestBody @Valid UrlUploadDTO uploadDTO) {
        OssStorageService storageService = OssStorageFactory.getStorageService();
        HttpResponse response = null;
        InputStream inputStream = null;
        try {
            // 获取 HTTP 响应头信息
            response = HttpRequest.get(uploadDTO.getUrl()).execute();
            inputStream = response.bodyStream();
            // 获取文件名
            String fileName = extractFileName(response);
            String path = uploadDTO.getPathFlag() + "/" + uploadDTO.getPathPrefix() + "/" +
                    YitIdHelper.nextId() + SysConstant.SEPARATOR + fileName;
            String upload = storageService.upload(inputStream, path);
            return ResponseBean.ok(upload);
        } catch (Exception e) {
            log.error("[文件上传] --- 下载远程文件失败，URL: {}, errorInfo: {}", uploadDTO.getUrl(), e.toString());
            return ResponseBean.error(ServiceErrorCodes.FILE_UPLOAD_FAILED, null);
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(response);
        }
    }

    /**
     * 从 HTTP 响应头中提取文件名，如果没有则根据 Content-Type 推断后缀生成文件名
     */
    private static String extractFileName(HttpResponse response) {
        String fileName = null;
        String contentDisposition = response.header("Content-Disposition");
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            try {
                fileName = contentDisposition.split("filename=")[1].replace("\"", "");
                fileName = URLDecoder.decode(fileName, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
                System.err.println("文件名解析失败: " + e.getMessage());
            }
        }
        if (fileName == null) {
            String contentType = response.header("Content-Type");
            // 使用 Content-Type 推断后缀生成文件名
            String fileExtension = MimeTypeUtils.parseMimeType(contentType).getSubtype();
            fileName = "remote_file." + fileExtension;
        }
        return fileName;
    }
}
