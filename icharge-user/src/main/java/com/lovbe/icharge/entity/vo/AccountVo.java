package com.lovbe.icharge.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/29 11:33
 * @Description: MS
 */
@Data
@Accessors(chain = true)
public class AccountVo {
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
     * 个人主页路径
     */
    private String domain;
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
