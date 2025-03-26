package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * 验证码发送记录
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2025/3/25 20:48
 */
@Data
@Accessors(chain = true)
public class CodeSendDTO {
    /**
     * 发送记录
     */
    private Long uid;
    /**
     * 场景code
     */
    private Integer scene;
    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 发送标题
     */
    private String title;
    /**
     * 邮箱模板内容
     */
    private String templateContent;
    /**
     * 短信模板id
     */
    private String templateId;
    /**
     * 模板参数
     */
    private Map<String, Object> templateParam;
}
