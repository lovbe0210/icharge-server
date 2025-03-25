package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 验证码发送记录
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2025/3/25 20:48
 */
@Data
@Accessors(chain = true)
public class VerifyCodeDTO {
    /**
     * 发送id
     */
    private Long sendId;
    /**
     * 场景code
     */
    private String sceneCode;
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
     * 模板内容
     */
    private String content;
    /**
     * 发送状态
     */
    private Integer sendStatus;
    /**
     * 发送时间
     */
    private Date sendTime;
}
