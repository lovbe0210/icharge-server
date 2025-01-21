package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/17 0:08
 * @Description: 优秀作者
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserVo extends UserInfoDo {
    /**
     * 高亮展示用户名
     */
    private String showUsername;
}
