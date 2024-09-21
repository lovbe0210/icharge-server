package com.lovbe.icharge.common.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lovbe.icharge.common.config.CodeToEnumDeserializer;

/**
 * @Author: lovbe0210
 * @Date: 2024/9/16 23:25
 * @Description: 基础枚举，用于统一管理自动转换
 */
@JsonDeserialize(using = CodeToEnumDeserializer.class)
public interface BaseEnum {
    Object getCode();
}
