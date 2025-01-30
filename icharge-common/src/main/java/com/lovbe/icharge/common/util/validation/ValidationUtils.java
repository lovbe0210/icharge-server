package com.lovbe.icharge.common.util.validation;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

import static com.lovbe.icharge.common.util.CommonUtils.bitwiseInvert;

/**
 * 校验工具类
 *
 * @author 芋道源码
 */
public class ValidationUtils {
    public static final String MOBILE_REGEXP = "^(?:(?:\\+|00)86)?1[3-9]\\d{9}$";

    private static final Pattern PATTERN_MOBILE = Pattern.compile(MOBILE_REGEXP);

    private static final Pattern PATTERN_PASSWORD = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\\\d)[^\\\\s]{8,30}$");

    private static final Pattern PATTERN_URL = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private static final Pattern PATTERN_XML_NCNAME = Pattern.compile("[a-zA-Z_][\\-_.0-9_a-zA-Z$]*");

    private static final Logger log = LoggerFactory.getLogger(ValidationUtils.class);

    public static boolean isMobile(String mobile) {
        return StringUtils.hasText(mobile)
                && PATTERN_MOBILE.matcher(mobile).matches();
    }

    public static boolean isPassword(String password) {
        return StringUtils.hasText(password)
                && PATTERN_PASSWORD.matcher(password).matches();
    }

    public static boolean isURL(String url) {
        return StringUtils.hasText(url)
                && PATTERN_URL.matcher(url).matches();
    }

    public static boolean isXmlNCName(String str) {
        return StringUtils.hasText(str)
                && PATTERN_XML_NCNAME.matcher(str).matches();
    }

    public static void validate(Object object, Class<?>... groups) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Assert.notNull(validator);
        validate(validator, object, groups);
    }

    public static void validate(Validator validator, Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (CollUtil.isNotEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    /**
     * @description: 滑块验证码
     * @param: String
     * @return: boolean
     * @author: lovbe0210
     * @date: 2024/9/14 13:49
     */
    public static boolean sliderValid(String value) {
        // 先转码然后反转base64
        try {
            String bitwised = bitwiseInvert(value);
            String decodedStr = Base64.decodeStr(bitwised);
            JSONObject parseObj = JSONUtil.parseObj(decodedStr);
            String tn = parseObj.getStr(SysConstant.TN);
            String uniqueId = parseObj.getStr(SysConstant.UNIQUE_ID);
            String svScene = parseObj.getStr(SysConstant.SV_SCENE);
            String redisKey = RedisKeyConstant.getSvTokenKey(uniqueId, svScene, tn);
            if (RedisUtil.get(redisKey) == null) {
                return false;
            }
            return true;
        }catch (Exception e) {
            log.error("[滑块验证] --- 滑块解析失败，sourceValue：{}", value);
        }
        return false;
    }




    public static void main(String[] args) {
//        String sss = "eyJ1bmlxdWVfaWQiOiJiYWNiOTJjYjQ4M2M0N2JjODQxNzE5ZjZkMmYxMDYwMCIsInRpbWVzdGFtcCI6MTcyNjQxOTc5Nzg4Mn0=";
        String sss = "eyJ0biI6IjRlYjE4NDYzZjAwOTRiOWU5N2JhZGFkNzQ2NmFjZjk5In0=";
        System.out.println(bitwiseInvert(sss));
    }
}
