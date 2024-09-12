package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import lombok.Data;

import java.util.List;

/**
 * @Author: lovbe
 * @Date: 2024/8/18 20:51
 * @Description: 验证码模板Do
 */
@Data
public class VCodeTemplateDO {
    /**
     * 自增编号
     */
    private long uid;

    // ========= 模板相关字段 =========
    /**
     * 启用状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private String status;
    /**
     * 模板编码，保证唯一
     *
     * 枚举 {@link CodeSceneEnum}
     */
    private String code;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板内容
     *
     * 内容的参数，使用 {} 包括，例如说 {name}
     */
    private String content;
    /**
     * 参数数组(自动根据内容生成)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> params;
    /**
     * 备注
     */
    private String remark;
}
