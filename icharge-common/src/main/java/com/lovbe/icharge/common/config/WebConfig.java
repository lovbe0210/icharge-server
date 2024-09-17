package com.lovbe.icharge.common.config;

import com.lovbe.icharge.common.enums.BaseEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: lovbe0210
 * @Date: 2024/9/16 23:23
 * @Description: MS
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new ConverterFactory<Object, BaseEnum>() {
            @Override
            public <T extends BaseEnum> Converter<Object, T> getConverter(Class<T> targetType) {
                T[] enums = targetType.getEnumConstants();
                return source -> {
                    for (T e : enums) {
                        if (e.getCode().equals(source)) {
                            return e;
                        }
                    }
                    return null;
                };
            }
        });
    }
}
