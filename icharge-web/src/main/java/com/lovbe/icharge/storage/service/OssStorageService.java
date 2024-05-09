package com.lovbe.icharge.storage.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.lovbe.icharge.storage.config.OssStorageConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @description: Oss基础客户端
 * @author: Lvhl
 * @date: 2024/5/9 13:20
 */
public abstract class OssStorageService {

    /**
     * oss配置
     */
    public OssStorageConfig config;


    /**
     * 文件上传
     *
     * @param is   文件
     * @param path 相对于存储系统的路径
     * @return 文件地址
     */
    public abstract String upload(InputStream is, String path);

    /**
     * 文件上传
     *
     * @param data 文件 上传路径
     * @param path 相对于存储系统的路径
     * @return 文件地址
     */
    public abstract String upload(byte[] data, String path);

    /**
     * description: 文件下载
     * @author: Lvhl
     * @date: 2023/11/28 16:51
     * @param path 文件路径
     * @return InputStream
     */
    public abstract InputStream downloadFile(String path);


    /**
     * 下载文件
     *
     * @param url 文件地址
     * @return 文件
     */
    public InputStream download(String url) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpUtil.download(url, output, false);
        return new ByteArrayInputStream(output.toByteArray());
    }

    /**
     * 删除文件
     *
     * @param path 相对于存储系统的路径
     */
    public abstract void delete(String path);

    /**
     * 获取完整路径 包括前缀等
     *
     * @param filePath 文件1
     */
    public String fullPath(String filePath) {
        filePath = StrUtil.addPrefixIfNot(filePath, "/");
        String prefixPath = StrUtil.emptyToDefault(config.getPrefixPath(), StrUtil.EMPTY);
        return prefixPath + filePath;
    }

    /**
     * 获取可访问链接
     *
     * @return url
     */
    public String getUrl(String filePath) {
        return StrUtil.addPrefixIfNot(fullPath(filePath), "/");
    }

}
