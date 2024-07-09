package com.lovbe.icharge.storage.config;


import com.lovbe.icharge.util.SpringContextUtils;
import com.lovbe.icharge.storage.enums.OssTypeEnum;
import com.lovbe.icharge.storage.service.OssStorageService;
import com.lovbe.icharge.storage.service.QiniuCloudStorageService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @description: 文件上传Factory
 * @author: Lvhl
 * @date: 2024/5/9 18:47
 */
@Slf4j
public final class OssStorageFactory {
    @Getter
    private static OssStorageService storageService;
    @Getter
    private static OssStorageConfig config;

    static {
        build();
    }

    public static synchronized void build() {
        OssStorageConfig config = SpringContextUtils.getBean(OssStorageConfig.class);
        if(Objects.isNull(config)) {
            return;
        }
        if (config.getOssType() == OssTypeEnum.QINIU) {
            storageService = new QiniuCloudStorageService(config);
        }
        /*
       else if (config.getOssType() == OssTypeEnum.ALIYUN) {
            storageService = new AliyunOssStorageService(config);
        } else if (config.getOssType() == OssTypeEnum.UPYUN) {
            storageService = new UpyunStorageService(config);
        } else if (config.getOssType() == OssTypeEnum.LOCAL) {
            storageService = new LocalStorageService(config);
        } else if (config.getOssType() == OssTypeEnum.S3) {
            storageService = new S3StorageService(config);
        }*/
        OssStorageFactory.config = config;
    }

}
