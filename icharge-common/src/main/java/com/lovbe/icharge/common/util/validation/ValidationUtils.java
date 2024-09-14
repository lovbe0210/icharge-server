package com.lovbe.icharge.common.util.validation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 校验工具类
 *
 * @author 芋道源码
 */
public class ValidationUtils {
    public static final String MOBILE_REGEXP = "^(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[0,1,4-9])|(?:5[0-3,5-9])|(?:6[2,5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[0-3,5-9]))\\d{8}$";

    private static final Pattern PATTERN_MOBILE = Pattern.compile(MOBILE_REGEXP);

    private static final Pattern PATTERN_URL = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private static final Pattern PATTERN_XML_NCNAME = Pattern.compile("[a-zA-Z_][\\-_.0-9_a-zA-Z$]*");

    public static boolean isMobile(String mobile) {
        return StringUtils.hasText(mobile)
                && PATTERN_MOBILE.matcher(mobile).matches();
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
        return false;
    }

    public static void main(String[] args) {
        long l1 = System.currentTimeMillis();
        String originalString = "Hello, World!<div><p>这是滑 动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>Hello, World!<div><p>这是滑动验证码</p><div/>";
        String base64EncodedString = base64Encode(originalString);
        System.out.println("Base64 Encoded: " + base64EncodedString);
        long l2 = System.currentTimeMillis();
        String decodeStr = bitwiseInvert(base64EncodedString);
        System.out.println("After Bitwise Inversion: " + decodeStr);
        long l3 = System.currentTimeMillis();
        String sourceBase64 = bitwiseInvert(decodeStr);
        String sourceStr = base64Decode(sourceBase64);
        System.out.println("After Base64 Decode: " + sourceStr);
        long l4 = System.currentTimeMillis();
        System.out.println("l2-l1:" + (l2 - l1));
        System.out.println("l3-l2:" + (l3 - l2));
        System.out.println("l4-l3:" + (l4 - l3));
    }

    public static String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String bitwiseInvert(String input) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            // 异或操作
            int invertedChar = c ^ 101;
            chars[i] = (char) invertedChar;
        }
        return new String(chars);
    }

    public static String base64Decode(String input) {
        byte[] decodedBytes = Base64.getDecoder().decode(input);
        return new String(decodedBytes);
    }
}
