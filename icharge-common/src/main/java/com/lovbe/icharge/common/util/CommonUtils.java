package com.lovbe.icharge.common.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
            // 只针对数字或字母异或操作，对于异或之后超出数字或字母范围的还是使用原字符
            if (Character.isLetterOrDigit(c) && Character.isLetterOrDigit(c ^ 1)) {
                int invertedChar = c ^ 1;
                chars[i] = (char) invertedChar;
            }
        }
        return new String(chars);
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

    public static String getContentTextValue(JSONObject parseObj) {
        StringBuilder builder = new StringBuilder();
        recursiveParseText(builder, parseObj);
        return builder.toString();
    }

    static void recursiveParseText(StringBuilder builder, JSONObject child) {
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
}
