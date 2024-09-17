package com.lovbe.icharge.common.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum CommonStatusEnum implements BaseEnum {

    NORMAL("A", "启用"),
    DELETE("D", "删除"),
    DISABLE("S", "停用/封禁/审核不通过");

    /**
     * 状态值
     */
    private final String status;
    /**
     * 状态名
     */
    private final String name;

    public static boolean isNormal(String status) {
        return ObjUtil.equal(NORMAL.status, status);
    }

    public static boolean isDelete(String status) {
        return ObjUtil.equal(DELETE.status, status);
    }

    public static boolean isDISABLE(String status) {
        return ObjUtil.equal(DISABLE.status, status);
    }

    @Override
    public Object getCode() {
        return status;
    }
}
