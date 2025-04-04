package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.MenuDTO;
import com.lovbe.icharge.common.model.dto.UploadDTO;
import com.lovbe.icharge.common.model.dto.UrlUploadDTO;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.service.ContentService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/24 23:41
 * @Description: 公共接口
 */
@RestController
public class CommonController {
    @Resource
    private CommonService commonService;
    @Resource
    private ContentService contentService;
    /**
     * description: 获取菜单码表
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/menu")
    public ResponseBean<MenuDTO> getMenuCode() {
        return ResponseBean.ok(commonService.getMenuList());
    }

    /**
     * @description: 内容文件上传（文章、随笔、个人主页内容文件）
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/9 17:28
     */
    @PostMapping("/content/upload/file")
    public ResponseBean uploadContentFile(@Valid UploadDTO uploadDTO,
                                          @RequestHeader(value = SysConstant.USERID) Long userId) {
        MultipartFile attachFile = uploadDTO.getFile();
        if (attachFile == null) {
            throw new ServiceException(ServiceErrorCodes.FILE_IS_NULL);
        }
        if (attachFile.getSize() > SysConstant.SIZE_50MB) {
            throw new ServiceException(ServiceErrorCodes.FILE_OUT_SIZE_50);
        }
        String path = contentService.uploadContentFile(uploadDTO, userId);
        return ResponseBean.ok(path);
    }

    /**
     * @description: 通过url上传文件
     * @param: uploadDTO
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/3/11 22:25
     */
    @PostMapping("/content/upload/url")
    public ResponseBean upload(@RequestBody @Valid UploadDTO uploadDTO,
                               @RequestHeader(value = SysConstant.USERID) Long userId) {
        String fileUrl = uploadDTO.getUrl();
        if (!StringUtils.hasText(fileUrl)) {
            throw new ServiceException(ServiceErrorCodes.FILE_URL_IS_NULL);
        }
        if (!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://") && fileUrl.startsWith("/oss")) {
            String url = fileUrl.replace("/oss", "") + "?ts=" + System.currentTimeMillis();
            return ResponseBean.ok(url);
        }
        String path = contentService.uploadContentUrl(uploadDTO, userId);
        return ResponseBean.ok(path);
    }
}
