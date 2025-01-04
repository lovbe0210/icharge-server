package com.lovbe.icharge.common.annotation;

import java.lang.annotation.*;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/5 1:01
 * @Description: es索引名称
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface EsFieldType {
    /**
     * es索引字段类型
     */
    String value() default "";

    /**
     * es索引拆分细粒度
     */
    String analyzer() default "ik_max_word";

    /**
     * es索引搜索字段拆分细粒度
     */
    String searchAnalyzer() default "ik_smart";
}
