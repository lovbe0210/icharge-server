package com.lovbe.icharge.common.annotation;

import java.lang.annotation.*;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/5 1:01
 * @Description: es索引名称
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface EsIndex {
    /**
     * es索引名称
     */
    String value() default "";
}
