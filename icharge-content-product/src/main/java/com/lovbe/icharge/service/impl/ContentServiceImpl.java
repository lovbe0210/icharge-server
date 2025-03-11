package com.lovbe.icharge.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lovbe.icharge.common.config.ServiceProperties;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.common.model.dto.UploadDTO;
import com.lovbe.icharge.common.model.dto.UrlUploadDTO;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.service.ContentService;
import com.lovbe.icharge.service.feign.StorageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/11 22:18
 * @Description: MS
 */
@Slf4j
@Service
public class ContentServiceImpl implements ContentService {
    @Resource
    private ServiceProperties serviceProperties;
    @Resource
    private StorageService storageService;

    @Override
    public String uploadContentFile(UploadDTO uploadDTO, Long userId) {
        CommonUtils.checkUploadFrequencyLimit(String.valueOf(userId),
                SysConstant.FILE_SCENE_content, serviceProperties.getUploadLimit());
        String pathFlag = String.valueOf(userId);
        ResponseBean<String> responseBean = storageService
                .upload(new FileUploadDTO(uploadDTO.getFile(), SysConstant.FILE_SCENE_content, pathFlag));
        if (responseBean != null && responseBean.isResult()) {
            return responseBean.getData();
        } else {
            throw new ServiceException(ServiceErrorCodes.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public String uploadContentUrl(UploadDTO uploadDTO, Long userId) {
        CommonUtils.checkUploadFrequencyLimit(String.valueOf(userId),
                SysConstant.FILE_SCENE_content, serviceProperties.getUploadLimit());
        HttpResponse response = null;
        try {
            // 获取 HTTP 响应头信息
            response = HttpRequest.get(uploadDTO.getUrl()).execute();
            String contentLength = response.header("Content-Length");
            long fileSize = (contentLength != null) ? Long.parseLong(contentLength) : -1;
            if (fileSize > SysConstant.SIZE_100MB) {
                throw new ServiceException(ServiceErrorCodes.FILE_OUT_SIZE_100);
            }
            ResponseBean<String> responseBean = storageService.upload(new UrlUploadDTO(uploadDTO.getUrl(),
                    SysConstant.FILE_SCENE_content, String.valueOf(userId)));
            if (responseBean != null && responseBean.isResult()) {
                return responseBean.getData();
            } else {
                throw new ServiceException(JsonUtils.toJsonString(responseBean));
            }
        } catch (Exception e) {
            log.error("[文件上传] --- 下载远程文件失败，URL: {}, errorInfo: {}", uploadDTO.getUrl(), e.toString());
            throw new ServiceException(ServiceErrorCodes.FILE_UPLOAD_FAILED);
        } finally {
            IoUtil.close(response);
        }
    }
}
