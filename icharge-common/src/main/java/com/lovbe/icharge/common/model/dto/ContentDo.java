package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 专栏
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/10/11 22:19
 */
@Data
@TableName(value = "c_content")
@Accessors(chain = true)
public class ContentDo extends BaseEntity {
    /**
     * 非结构化内容
     */
    private String content;
}



