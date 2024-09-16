package com.lovbe.icharge.dto.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2024/9/15 8:53
 * @Description: 滑块验证埋点数据
 */
@Data
public class SecServiceSvVo {
    // 数据载体
    @NotBlank(message = "error request")
    private String sign;
    // 干扰项
    @NotBlank(message = "error request")
    private String xa;
    // 干扰项
    @NotBlank(message = "error request")
    private String xt;
    // 干扰项
    @NotBlank(message = "error request")
    private String efy;
}
