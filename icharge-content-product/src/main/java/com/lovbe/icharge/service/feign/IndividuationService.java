package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.common.model.dto.PreferenceSettingVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "individuation", fallback = IndividuationServiceFallback.class)
public interface IndividuationService {

    /**
     * @description: 获取偏好设置
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.common.model.dto.PreferenceSettingVo>
     * @author: lovbe0210
     * @date: 2025/2/9 17:28
     */
    @GetMapping(value = "/api/in/preference/settings")
    ResponseBean<PreferenceSettingVo> getPreferenceSetting(@RequestHeader(SysConstant.USERID) Long userId);
}
