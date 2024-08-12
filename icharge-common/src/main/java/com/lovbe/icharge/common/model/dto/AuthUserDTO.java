package com.lovbe.icharge.common.model.dto;

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
    private String mobile;
    private String email;
    private Integer loginType;
    private String userIp;
}
