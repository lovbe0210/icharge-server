package com.lovbe.icharge.common.model.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AccountInfo {
    private String uid;
    private String mobile;
    private String email;
    private String password;
    private String status;
    private Date createTime;
    private Date updateTime;
}
