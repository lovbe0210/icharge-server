package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

@TableName(value = "p_account")
@Data
@Accessors(chain = true)
public class AccountDo extends BaseEntity {
    private String mobile;
    private String email;
    private String password;
    private Integer loginCount;
    private Date lastLoginTime;
    private String lastLoginIp;
    private String loginAgent;
    private String loginOs;
}
