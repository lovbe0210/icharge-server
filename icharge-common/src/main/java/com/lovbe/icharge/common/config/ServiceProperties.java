package com.lovbe.icharge.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/12 0:07
 * @Description: 配置属性
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "service")
public class ServiceProperties {
    /**
     * domain过滤，保留一些菜单值和特殊字符
     */
    private Set<String> domainFilter = new HashSet<>();
    /**
     * qq登录appId
     */
    private String qqAppId;
    /**
     * qq登录appKey
     */
    private String qqAppKey;
    /**
     * qq登录重定向地址
     */
    private String qqRedirectUrl;
    /**
     * 文件上传限制，每小时20次，从第一次上传开始计时，具体由各个服务自己控制
     */
    private int uploadLimit = 20;
    /**
     * 封面上传频率限制
     */
    private int coverUploadLimit = 20;
    /**
     * 验证码发送滚动每小时5条后开始限制
     */
    private int verifyCodeLimit = 5;
    /**
     * 经验等级
     */
    private int level1Exp;
    private int level2Exp;
    private int level3Exp;
    private int level4Exp;
    private int level5Exp;
    private int level6Exp;

    /**
     * 验证码有效时间
     */
    private int minutes = 10;

    // =================== 邮箱参数 =======================
    /**
     * 系统邮箱地址
     */
    private String serverEmail;
    /**
     * 邮箱服务用户名
     */
    private String emailUsername;
    /**
     * 邮箱服务密码
     */
    private String emailPassword;
    /**
     * 邮箱服务器host
     */
    private String emailHost;
    /**
     * 邮箱服务器port
     */
    private Integer emailPort;
    /**
     * 是否开启 SSL
     */
    private Boolean sslEnable;
    /**
     * 是否开启 STARTTLS
     */
    private Boolean starttlsEnable;

    // =================== 短信参数 =======================
    /**
     * 短信发送请求地址
     */
    private String smsUrl;
    /**
     * 短信服务请求方式
     */
    private String smsRequestMethod;
    /**
     * 短信服务商生成的AppCode
     */
    private String smsAppcode;
    /**
     * 短信签名id
     */
    private String smsSignId;
    /**
     * 短信模板id
     */
    private String templateId;
    /**
     * latex转换接口地址
     */
    private String latexUrl;
}
