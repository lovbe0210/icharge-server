package com.lovbe.icharge.common.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Random;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/5 0:16
 * @Description: 公共工具类
 */
public class CommonUtils {
    private static byte[] key = new byte[]{2, -115, 44, -80, 32, -60, -55, -103, 37, 54, 30, 72, -121, -103, 107, 123};
    private static AES aes;
    private static EnumMap<DayOfWeek, String> CHINESE_WEEKDAYS = new EnumMap<>(DayOfWeek.class);

    static {
        aes = SecureUtil.aes(key);
        CHINESE_WEEKDAYS.put(DayOfWeek.MONDAY, "周一");
        CHINESE_WEEKDAYS.put(DayOfWeek.TUESDAY, "周二");
        CHINESE_WEEKDAYS.put(DayOfWeek.WEDNESDAY, "周三");
        CHINESE_WEEKDAYS.put(DayOfWeek.THURSDAY, "周四");
        CHINESE_WEEKDAYS.put(DayOfWeek.FRIDAY, "周五");
        CHINESE_WEEKDAYS.put(DayOfWeek.SATURDAY, "周六");
        CHINESE_WEEKDAYS.put(DayOfWeek.SUNDAY, "周日");
    }
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

    /**
     * @description: 按位异或一个固定值，得到转码之后的数据，可再次异或得到原值
     * @param: String
     * @return: String
     * @author: lovbe0210
     * @date: 2024/12/29 23:36
     */
    public static String bitwiseInvert(String input) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (!Character.isLetterOrDigit(c)) {
                continue;
            }
            // 只针对数字或字母异或操作，对于异或之后超出数字或字母范围的还是使用原字符
            int invertedChar = c ^ 127;
            if (Character.isLetterOrDigit(invertedChar)) {
                chars[i] = (char) invertedChar;
            }
        }
        return new String(chars);
    }

    public static String encryptStr(String sourceStr) {
        return aes.encryptHex(sourceStr);
    }

    public static String decryptStr(String sourceStr) {
        return aes.decryptStr(sourceStr);
    }

    /**
     * @description: 上传频率限制
     * @param: uniqueId
     * @param: userId
     * @param: limitTimes
     * @author: lovbe0210
     * @date: 2025/3/11 21:29
     */
    public static void checkUploadFrequencyLimit(String uniqueId, String scene, int limitTimes) {
        // 唯一id频率校验
        String uploadLockKey = RedisKeyConstant.getUploadLockKey(scene, uniqueId);
        Object limit = RedisUtil.get(uploadLockKey);
        if (limit != null &&  (Integer) limit > limitTimes) {
            throw new ServiceException(ServiceErrorCodes.FILE_UPLOAD_LIMIT);
        } else {
            RedisUtil.incr(uploadLockKey, 1);
            if (limit == null) {
                RedisUtil.expire(uploadLockKey, SysConstant.HOUR_1);
            }
        }
        // ip地址限制
        String clientIP = ServletUtils.getClientIP();
        if (clientIP != null) {
            uploadLockKey = RedisKeyConstant.getUploadLockKey(scene, clientIP);
            limit = RedisUtil.get(uploadLockKey);
            if (limit != null &&  (Integer) limit > limitTimes) {
                throw new ServiceException(ServiceErrorCodes.FILE_UPLOAD_LIMIT);
            } else {
                RedisUtil.incr(uploadLockKey, 1);
                if (limit == null) {
                    RedisUtil.expire(uploadLockKey, SysConstant.HOUR_1);
                }
            }
        }
    }

    /**
     * @description: 用户状态检查，返回可用的userInfoDo
     * @param: UserInfoDo
     * @return: UserInfoDo
     * @author: lovbe0210
     * @date: 2024/12/29 23:36
     */
    public static UserInfoDo checkUserStatus(UserInfoDo userInfo) {
        if (userInfo == null) {
            userInfo = new UserInfoDo()
                    .setUsername(SysConstant.UNKNOWN_USER_NAME)
                    .setGrowthValue(0)
                    .setLevel(0)
                    .setDomain(SysConstant.UNKNOWN_USER_NAME);
        } else if (CommonStatusEnum.isDisable(userInfo.getStatus())) {
            userInfo = new UserInfoDo()
                    .setUsername(SysConstant.DISABLE_USER_NAME)
                    .setGrowthValue(0)
                    .setLevel(0)
                    .setDomain(SysConstant.DISABLE_USER_NAME);
            userInfo.setStatus(userInfo.getStatus())
                    .setUid(userInfo.getUid());
        } else if (CommonStatusEnum.isDelete(userInfo.getStatus())) {
            userInfo = new UserInfoDo()
                    .setUsername(SysConstant.LOGOFF_USER_NAME)
                    .setGrowthValue(0)
                    .setLevel(0)
                    .setDomain(SysConstant.LOGOFF_USER_NAME);
            userInfo.setStatus(userInfo.getStatus())
                    .setUid(userInfo.getUid());
        }
        return userInfo;
    }

    /**
     * @description: 解析文章内容为纯文本
     * @param: JSONObject
     * @return: String
     * @author: lovbe0210
     * @date: 2025/1/11 23:29
     */
    public static String getContentTextValue(JSONObject parseObj) {
        StringBuilder builder = new StringBuilder();
        recursiveParseText(builder, parseObj);
        return builder.toString();
    }

    /**
     * 递归获取文本内容
     * @param builder
     * @param child
     */
    public static void recursiveParseText(StringBuilder builder, JSONObject child) {
        if (child == null) {
            return;
        }
        // 获取children
        JSONArray jsonArray = child.getJSONArray(SysConstant.CHILDREN);
        if (!CollectionUtils.isEmpty(jsonArray)) {
            for (Object object : jsonArray) {
                recursiveParseText(builder, (JSONObject) object);
            }
        } else {
            // 没有下级或下级为空是获取text文本
            String text = child.getStr(SysConstant.TEXT);
            if (StringUtils.hasLength(text)) {
                text = text.replaceAll("\\s+", " ");
                text = text.replaceAll("\\r?\\n", "");
                if (text.length() > 0) {
                    builder.append(text);
                }
            }
        }
    }

    /**
     * @description: 将 LocalDate 转换为中文星期名称
     * @param: date
     * @return: java.lang.String
     * @author: lovbe0210
     * @date: 2025/3/17 21:58
     */
    public static String weekday(LocalDate date) {
        return CHINESE_WEEKDAYS.get(date.getDayOfWeek());
    }
}
