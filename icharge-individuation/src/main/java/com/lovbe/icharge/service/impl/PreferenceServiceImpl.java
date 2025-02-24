package com.lovbe.icharge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.dao.MusicPlayDao;
import com.lovbe.icharge.dao.PreferenceSettingDao;
import com.lovbe.icharge.entity.MusicInfoVo;
import com.lovbe.icharge.entity.PreferenceSettingDTO;
import com.lovbe.icharge.common.model.dto.PreferenceSettingVo;
import com.lovbe.icharge.service.PreferenceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/9 17:07
 * @Description: MS
 */
@Service
public class PreferenceServiceImpl implements PreferenceService {
    @Resource
    private PreferenceSettingDao preferenceSettingDao;

    @Override
    public PreferenceSettingVo getPreferenceSetting(Long userId) {
        PreferenceSettingVo settingVo = preferenceSettingDao.selectById(userId);
        if (settingVo == null) {
            initPreferenceSetting(userId);
            return preferenceSettingDao.selectById(userId);
        }
        return settingVo;
    }

    @Override
    public void initPreferenceSetting(Long userId) {
        PreferenceSettingVo settingVo = new PreferenceSettingVo()
                .setConfigFrom(1)
                .setContentDefaultPublic(1)
                .setEnableComment(1)
                .setDocStyleDefaultFont(15)
                .setDocThemeSync(1)
                .setCustomTheme("""
                                {
                                    "themeColor":"rgba(255,255,255,1)",
                                    "fontColor":"#262626",
                                    "titleColor":"#585A5A",
                                    "backgroundImg":"linear-gradient(45deg, #F4F5F7 0%, #F4F5F7 100%)",
                                    "borderColor":"rgba(0,0,0,0.08)",
                                    "dropdownBackgroundColor":"#FFFFFF",
                                    "dropdownItemHover":"#F8F8F8",
                                    "onThemeBackgroundColor":"#FAFAFA",
                                    "ramblyJotEditorBackgroundColor":"#F2F3F5",
                                    "modalBackgroundColor":"#FFFFFF"
                                }
                                """);
        settingVo.setUid(userId);
        preferenceSettingDao.insert(settingVo);
    }

    @Override
    public void updatePreferenceSetting(PreferenceSettingDTO data, Long userId) {
        PreferenceSettingVo settingVo = preferenceSettingDao.selectById(userId);
        if (settingVo == null) {
            throw new ServiceException(ServiceErrorCodes.AUTH_USER_STATUS_ERROR);
        }
        settingVo.setUpdateTime(new Date());
        if (data.getConfigFrom() != null) {
            settingVo.setConfigFrom(data.getConfigFrom());
        }
        if (data.getContentDefaultPublic() != null) {
            settingVo.setContentDefaultPublic(data.getContentDefaultPublic());
        }
        if (data.getEnableComment() != null) {
            settingVo.setEnableComment(data.getEnableComment());
        }
        if (data.getAutoPublish() != null) {
            settingVo.setAutoPublish(data.getAutoPublish());
        }
        if (data.getDocStyleDefaultFont() != null) {
            settingVo.setDocStyleDefaultFont(data.getDocStyleDefaultFont());
        }
        if (data.getDocStylePageSize() != null) {
            settingVo.setDocStylePageSize(data.getDocStylePageSize());
        }
        if (data.getDocThemeSync() != null) {
            settingVo.setDocThemeSync(data.getDocThemeSync());
        }
        if (!Objects.isNull(data.getCustomTheme())) {
            settingVo.setCustomTheme(JsonUtils.toJsonString(data.getCustomTheme()));
        }
        if (!Objects.isNull(data.getFlagContent())) {
            settingVo.setFlagContent(JsonUtils.toJsonString(data.getFlagContent()));
        }
        if (!Objects.isNull(data.getMusicPlay())) {
            settingVo.setMusicPlay(JsonUtils.toJsonString(data.getMusicPlay()));
        }
        preferenceSettingDao.updateById(settingVo);
    }
}
