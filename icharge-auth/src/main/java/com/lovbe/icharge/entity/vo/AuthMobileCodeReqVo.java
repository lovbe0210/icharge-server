package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.validation.Mobile;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @description: 手机发送验证码
 * @author: Lvhl
 * @date: 2024/8/7 13:37
 */
@Data
public class AuthMobileCodeReqVo {
    @NotBlank(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @NotBlank(message = "请先完成滑块验证")
    private String sliderVerification;

    private CodeSceneEnum codeScene;
}
