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
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 密码
     */
    private String password;
    /**
     * 登录次数
     */
    private Integer loginCount;
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    /**
     * 最后登录ip
     */
    private String lastLoginIp;
    /**
     * 登录浏览器标识符
     */
    private String loginAgent;
    /**
     * 登录系统
     */
    private String loginOs;
    /**
     * 微信id
     */
    private String wechatOpenId;
    /**
     * qqid
     */
    private String qqOpenId;
    /**
     * githubid
     */
    private String githubOpenId;
    /**
     * 谷歌id
     */
    private String googleOpenId;
}
