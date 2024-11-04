package com.lovbe.icharge.common.util;

import cn.hutool.core.util.IdUtil;

import java.util.Random;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/5 0:16
 * @Description: 公共工具类
 */
public class CommonUtils {
    /**
     * @description 获取指定长度的字母数字组合
     * @param[1] length
     * @return String
     * @author lovbe0210
     * @date 2024/11/5 0:25
     */
    public static String getLengthRandomString(int length) {
        length = length == 0 ? 6 : length;
        String uuid = IdUtil.simpleUUID();
        StringBuilder randomStr = new StringBuilder();
        Random random = new Random();
        for (int j = 0; j < length; j++) {
            int index = random.nextInt(uuid.length());
            randomStr.append(uuid.charAt(index));
        }
        return randomStr.toString();
    }

    public static String getBeautifulId() {
        return CommonUtils.getLengthRandomString(6);
    }
}
