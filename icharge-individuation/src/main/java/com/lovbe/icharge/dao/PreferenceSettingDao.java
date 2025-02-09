package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.PreferenceSettingVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/9 17:12
 * @Description: 偏好设置
 */
@Mapper
public interface PreferenceSettingDao extends BaseMapper<PreferenceSettingVo> {
    @Insert(value = """
                    INSERT INTO i_personalize_config (uid, config_from, content_default_public, enable_comment, auto_publish, 
                        doc_style_default_font, doc_style_page_size, doc_theme_sync, theme_color, background) 
                    VALUES (#{settingVo.uid}, #{settingVo.configFrom}, #{settingVo.contentDefaultPublic}, #{settingVo.enableComment}, 
                            #{settingVo.autoPublish}, #{settingVo.docStyleDefaultFont}, #{settingVo.docStylePageSize}, 
                            #{settingVo.docThemeSync},  #{settingVo.themeColor}, #{settingVo.background})
                    ON DUPLICATE KEY UPDATE 
                        config_from = #{settingVo.configFrom},
                        content_default_public = #{settingVo.contentDefaultPublic},
                        enable_comment = #{settingVo.enableComment},
                        auto_publish = #{settingVo.autoPublish},
                        doc_style_default_font = #{settingVo.docStyleDefaultFont},
                        doc_style_page_size = #{settingVo.docStylePageSize},
                        doc_theme_sync = #{settingVo.docThemeSync},
                        theme_color = #{settingVo.themeColor},
                        background = #{settingVo.background}
                    """)
    void updateOrInsert(@Param("settingVo") PreferenceSettingVo settingVo);
}
