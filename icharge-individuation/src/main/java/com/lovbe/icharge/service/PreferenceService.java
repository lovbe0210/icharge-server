package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.MusicInfoVo;
import com.lovbe.icharge.entity.PreferenceSettingDTO;
import com.lovbe.icharge.common.model.dto.PreferenceSettingVo;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/9 17:07
 * @Description: 偏好设置
 */
public interface PreferenceService {
    /**
     * @description: 获取偏好设置
     * @param: userId
     * @return: com.lovbe.icharge.common.model.dto.PreferenceSettingVo
     * @author: lovbe0210
     * @date: 2025/2/9 17:09
     */
    PreferenceSettingVo getPreferenceSetting(Long userId);

    /**
     * @description: 偏好设置初始化
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/2/9 17:29
     */
    void initPreferenceSetting(Long userId);

    /**
     * @description: 偏好设置
     * @param: data
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/2/9 19:01
     */
    void updatePreferenceSetting(PreferenceSettingDTO data, Long userId);
}
