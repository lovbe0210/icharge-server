package com.lovbe.icharge.common.util;

import cn.hutool.core.collection.CollectionUtil;
import com.lovbe.icharge.common.model.base.ResponseBean;
import lombok.Data;

import java.util.Collection;

/**
 * @description: 远程调用请求结果判断
 * @author: Lvhl
 * @date: 2024/8/7 15:48
 */
@Data
public class FeignRequestUtils {
    public static <T> boolean checkResp(ResponseBean<T> responseBean) {
        if (responseBean == null || responseBean.getData() == null) {
            return false;
        }

        T data = responseBean.getData();
        if (data instanceof Collection && CollectionUtil.isEmpty((Collection<?>) data)) {
            return false;
        }

        return true;
    }
}
