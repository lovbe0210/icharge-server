package com.lovbe.icharge.common.validation;

import com.lovbe.icharge.common.util.validation.ValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @description: 密码校验逻辑
 * @author: Lvhl
 * @date: 2024/8/7 11:52
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return ValidationUtils.isPassword(value);
    }
}
