package com.lovbe.icharge.dto;

import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.validation.Mobile;
import com.lovbe.icharge.common.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Author: lovbe0210
 * @Date: 2024/9/16 14:41
 * @Description: 忘记密码
 */
@Data
public class ForgetPasswordDTO {
    @Mobile
    private String mobile;
    @Email
    private String email;
    @NotNull(message = "验证码使用场景不得为空")
    private CodeSceneEnum scene;
//    @Password
    private String password;
    @NotBlank(message = "验证码不得为空")
    @Length(min = 6, max = 6, message = "验证码错误")
    private String verifyCode;
}
