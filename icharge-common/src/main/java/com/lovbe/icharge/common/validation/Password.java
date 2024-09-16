package com.lovbe.icharge.common.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
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
 * @description: 手机校验注解
 * @author: Lvhl
 * @date: 2024/8/7 11:38
 */
public @interface Password {
    String message() default "密码格式不正确，必须包含数字和字母，长度8-30位";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
