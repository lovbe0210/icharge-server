package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.PreferenceSettingDTO;
import com.lovbe.icharge.common.model.dto.PreferenceSettingVo;
import com.lovbe.icharge.common.model.dto.UploadDTO;
import com.lovbe.icharge.service.PreferenceService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/9 1:21
 * @Description: 偏好设置
 */
@RestController
public class PreferenceController {
    @Resource
    private PreferenceService preferenceService;

    /**
     * @description: 获取偏好设置
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.common.model.dto.PreferenceSettingVo>
     * @author: lovbe0210
     * @date: 2025/2/9 17:28
     */
    @GetMapping("/preference/settings")
    public ResponseBean<PreferenceSettingVo> getPreferenceSetting(@RequestHeader(SysConstant.USERID) Long userId) {
        return ResponseBean.ok(preferenceService.getPreferenceSetting(userId));
    }

    /**
     * @description: 偏好设置初始化
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/9 17:28
     */
    @PutMapping("/preference/init")
    public ResponseBean initPreferenceSetting(@RequestHeader(SysConstant.USERID) Long userId) {
        preferenceService.initPreferenceSetting(userId);
        return ResponseBean.ok();
    }

    /**
     * @description: 偏好设置修改
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/9 17:28
     */
    @PostMapping("/preference/update")
    public ResponseBean updatePreferenceSetting(@RequestBody @Valid BaseRequest<PreferenceSettingDTO> baseRequest,
                                                @RequestHeader(SysConstant.USERID) Long userId) {
        preferenceService.updatePreferenceSetting(baseRequest.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * @description: 背景图片上传
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/9 17:28
     */
    @PostMapping("/preference/upload")
    public ResponseBean backgroundUpload(@Valid UploadDTO uploadDTO,
                                         @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        MultipartFile backgroundFile = uploadDTO.getFile();
        if (backgroundFile == null) {
            throw new ServiceException(ServiceErrorCodes.FILE_IS_NULL);
        }
        if (backgroundFile.getSize() > SysConstant.SIZE_10MB) {
            throw new ServiceException(ServiceErrorCodes.FILE_OUT_SIZE_10);
        }
        if (userId == null && !StringUtils.hasText(uploadDTO.getUq())) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        String path = preferenceService.uploadBackgroundImg(uploadDTO, userId);
        return ResponseBean.ok(path);
    }
}
