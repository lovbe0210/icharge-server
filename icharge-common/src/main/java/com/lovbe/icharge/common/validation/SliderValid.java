package com.lovbe.icharge.common.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SliderValidValidator.class)
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
/**
 * @description: 滑块验证注解
 * @author: Lvhl
 * @date: 2024/8/7 11:38
 */
public @interface SliderValid {
    String message() default "请先完成滑块验证";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
