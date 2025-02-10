package com.lovbe.icharge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/9 1:25
 * @Description: MS
 */
@Data
@Accessors(chain = true)
public class PreferenceSettingDTO {
    /**
     * 配置更新原则：配置项冲突时以本地或云端为主 1本地 0云端
     */
    @Min(value = 0, message = "设置项状态错误")
    @Max(value = 1, message = "设置项状态错误")
    private Integer configFrom;
    /**
     * 新建文章、专栏、随笔访问权限 0否1是
     */
    @Min(value = 0, message = "设置项状态错误")
    @Max(value = 1, message = "设置项状态错误")
    private Integer contentDefaultPublic;
    /**
     * 是否开启评论功能 0否1是
     */
    @Min(value = 0, message = "设置项状态错误")
    @Max(value = 1, message = "设置项状态错误")
    private Integer enableComment;
    /**
     * 自动发布/更新至阅读页 0否1是
     */
    @Min(value = 0, message = "设置项状态错误")
    @Max(value = 1, message = "设置项状态错误")
    private Integer autoPublish;
    /**
     * 文档默认字体大小
     */
    @Min(value = 12, message = "设置项状态错误")
    @Max(value = 24, message = "设置项状态错误")
    private Integer docStyleDefaultFont;
    /**
     * 页面布局 0标宽模式 1超宽模式
     */
    @Min(value = 0, message = "设置项状态错误")
    @Max(value = 1, message = "设置项状态错误")
    private Integer docStylePageSize;
    /**
     * 文档页面是否主题同步
     */
    @Min(value = 0, message = "设置项状态错误")
    @Max(value = 1, message = "设置项状态错误")
    private Integer docThemeSync;
    /**
     * 主题色
     */
    private String themeColor;
    /**
     * 背景色
     */
    private String background;


}
