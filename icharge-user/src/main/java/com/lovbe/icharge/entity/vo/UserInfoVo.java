package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/27 1:04
 * @Description: 用户信息vo
 */
@Data
@Accessors(chain = true)
public class UserInfoVo extends UserInfoDo {
    /**
     * 个人主页是否展示创作指数
     */
    private int domainHotmap;
    /**
     * 个人主页是否展示公开专栏
     */
    private int domainColumn;
}
