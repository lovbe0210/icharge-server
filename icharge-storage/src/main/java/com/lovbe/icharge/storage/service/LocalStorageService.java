package com.lovbe.icharge.storage.service;


import cn.hutool.core.io.FileUtil;
import com.lovbe.icharge.storage.config.OssStorageConfig;
import com.lovbe.icharge.storage.service.OssStorageService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;

/**
 * @description: 本地文件存储
 * @author: Lvhl
 * @date: 2024/7/30 12:28
 */
@Slf4j
public class LocalStorageService extends OssStorageService {
    public LocalStorageService(OssStorageConfig config) {
        this.config = config;
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        File file = FileUtil.file(config.getUploadFolder() + File.separator + path);
        //目录是否存在
        log.info("文件上传路径：{}", file.getAbsolutePath());
        if (!FileUtil.exist(file.getParent())) {
            FileUtil.mkdir(file.getParent());
        }
        log.info("文件上传路径：{}", file.getAbsolutePath());
        FileUtil.writeFromStream(inputStream, file);
        return getUrl(path);
    }

    @Override
    public String upload(byte[] data, String path) {
        File file = FileUtil.file(config.getUploadFolder() + File.separator + path);
        //目录是否存在
        if (!FileUtil.exist(file.getParent())) {
            FileUtil.mkdir(file.getParent());
        }
        FileUtil.writeBytes(data, file);
        return config.getDomain() + "/" + path;
    }

    @Override
    public InputStream downloadFile(String path) {
        return null;
    }

    @Override
    public String getPublicUrl(String path) {
        return null;
    }

    @Override
    public void delete(String path) {
        FileUtil.del(config.getUploadFolder() + File.separator + path);
    }
}
