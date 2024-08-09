package com.lovbe.icharge.common.validation;

import cn.hutool.core.util.StrUtil;
import com.lovbe.icharge.common.util.validation.ValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @description: 手机号校验逻辑
 * @author: Lvhl
 * @date: 2024/8/7 11:52
 */
public class MobileValidator implements ConstraintValidator<Mobile, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        // 如果手机号为空，则默认不校验，直接通过
        if (StrUtil.isBlankIfStr(value)) {
            return true;
        }
        return ValidationUtils.isMobile(value);
    }
}
