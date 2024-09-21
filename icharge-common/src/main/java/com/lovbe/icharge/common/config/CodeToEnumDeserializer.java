package com.lovbe.icharge.common.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lovbe.icharge.common.enums.BaseEnum;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author: lovbe0210
 * @Date: 2024/9/16 23:23
 * @Description: MS
 */
@Configuration
public class CodeToEnumDeserializer extends JsonDeserializer<BaseEnum> {

    @Override
    public BaseEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        final Integer param = jsonParser.getIntValue();
        final JsonStreamContext parsingContext = jsonParser.getParsingContext();
        final String currentName = parsingContext.getCurrentName();
        final Object currentValue = parsingContext.getCurrentValue();
        try {
            final Field declaredField = currentValue.getClass().getDeclaredField(currentName);
            final Class<?> targetType = declaredField.getType();
            final Method valuesMethod = targetType.getDeclaredMethod("values");
            BaseEnum[] enums = (BaseEnum[]) valuesMethod.invoke(null);
            for (BaseEnum anEnum : enums) {
                if(anEnum.getCode().equals(param)){
                    return anEnum;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
