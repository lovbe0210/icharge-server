package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/17 0:08
 * @Description: 优秀作者
 */
@Data
public class ExcellentUserVo extends UserInfoDo {
    /**
     * 是否关注
     */
    private int isFollow;
    /**
     * 高亮展示用户名
     */
    private String showUsername;
}
