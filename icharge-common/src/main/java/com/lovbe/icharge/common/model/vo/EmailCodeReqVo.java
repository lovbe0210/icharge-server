package com.lovbe.icharge.common.model.vo;

import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.validation.Mobile;
import com.lovbe.icharge.common.validation.SliderValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @description: 手机发送验证码
 * @author: Lvhl
 * @date: 2024/8/7 13:37
 */
@Data
public class EmailCodeReqVo {
    @NotBlank(message = "邮箱不能为空")
    @Email
    private String email;

    @NotBlank(message = "请先完成滑块验证")
    @SliderValid
    private String sign;

    @NotNull(message = "验证码发送场景不得为空")
    private CodeSceneEnum scene;
}
