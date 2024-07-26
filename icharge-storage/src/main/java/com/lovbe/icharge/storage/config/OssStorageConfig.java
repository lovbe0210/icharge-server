package com.lovbe.icharge.storage.config;


import com.lovbe.icharge.storage.enums.OssTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description: Oss配置
 * @author: Lvhl
 * @date: 2024/5/9 13:01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "oss")
public class OssStorageConfig {


    /**
     * oss 类型
     * 参考 OssTypeEnum.java
     */
    private OssTypeEnum ossType;


    /**
     * 阿里云：endpoint
     */
    private String endpoint;


    /**
     * accessKeyId
     */
    private String accessKey;

    /**
     * accessKeySecret
     */
    private String secretKey;


    /**
     * 桶名
     */
    private String bucketName;


    /**
     * 预览域名
     */
    private String domain;

    /**
     * oss 前缀目录
     */
    private String prefixPath;

    /**
     * 本地存储文件存放地址
     */
    private String uploadFolder;


    /**
     * 本地存储文件访问路径
     */
    private String accessPathPattern = "/u/**";


}
