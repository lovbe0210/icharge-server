package com.lovbe.icharge.common.validation;

import cn.hutool.core.util.StrUtil;
import com.lovbe.icharge.common.util.validation.ValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @description: 滑块验证校验逻辑
 * @author: Lvhl
 * @date: 2024/8/7 11:52
 */
public class SliderValidValidator implements ConstraintValidator<SliderValid, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isBlank(value)) {
            return false;
        }
        return ValidationUtils.sliderValid(value);
    }
}
