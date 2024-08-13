package com.lovbe.icharge.common.model.dto;

import com.lovbe.icharge.common.validation.Mobile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 登录用户相关DTO
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserDTO {
    @Mobile
    private String mobile;
    @Email(message = "邮箱格式不正确")
    private String email;
    private String password;
    @NotNull(message = "登录方式不得为空")
    private Integer loginType;
    @NotBlank(message = "登录Ip不得为空")
    private String userIp;
}
