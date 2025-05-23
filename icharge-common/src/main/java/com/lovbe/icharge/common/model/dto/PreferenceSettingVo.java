package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/9 1:25
 * @Description: MS
 */
@Data
@Accessors(chain = true)
@TableName("i_personalize_config")
public class PreferenceSettingVo extends BaseEntity {
    /**
     * 配置更新原则：配置项冲突时以本地或云端为主 1本地 0云端
     */
    private int configFrom;
    /**
     * 新建文章、专栏、随笔访问权限 默认公开 0否1是
     */
    private int contentDefaultPublic;
    /**
     * 是否开启评论功能 0否1是
     */
    private int enableComment;
    /**
     * 自动发布/更新至阅读页 0否1是
     */
    private int autoPublish;
    /**
     * 文档默认字体大小
     */
    private int docStyleDefaultFont;
    /**
     * 页面布局 0标宽模式 1超宽模式
     */
    private int docStylePageSize;
    /**
     * 文档页面是否主题同步
     */
    private int docThemeSync;
    /**
     * 自定义主题（json）
     */
    private String customTheme;
    /**
     * flag内容（json）
     */
    private String flagContent;
    /**
     * 音乐播放相关（json）
     */
    private String musicPlay;
    /**
     * 个人主页是否展示创作指数
     */
    private int domainHotmap;
    /**
     * 个人主页是否展示公开专栏
     */
    private int domainColumn;
}
