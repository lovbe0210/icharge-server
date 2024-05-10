package com.lovbe.icharge.storage.service;


import cn.hutool.core.io.IoUtil;
import com.lovbe.icharge.storage.config.OssStorageConfig;
import com.lovbe.icharge.storage.exception.StorageException;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.*;
import com.qiniu.util.Auth;

import java.io.InputStream;

/**
 * @description: 七牛云存储
 * @author: Lvhl
 * @date: 2024/5/9 13:09
 */
public class QiniuCloudStorageService extends OssStorageService {
    private final UploadManager uploadManager;
    private final Auth auth;
    private final Configuration cfg;

    public QiniuCloudStorageService(OssStorageConfig config) {
        this.config = config;
        cfg = new Configuration(Region.autoRegion());
        uploadManager = new UploadManager(cfg);
        auth = Auth.create(config.getAccessKey(), config.getSecretKey());

    }


    @Override
    public String upload(InputStream inputStream, String path) {
        byte[] bytes = IoUtil.readBytes(inputStream);
        return this.upload(bytes, path);
    }

    @Override
    public String upload(byte[] data, String path) {
        try {
            String token = auth.uploadToken(config.getBucketName());
            Response res = uploadManager.put(data, path, token);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res);
            }
        } catch (Exception e) {
            throw new StorageException("上传文件失败，请核对七牛配置信息", e);
        }
        return getUrl(path);
    }

    @Override
    public InputStream downloadFile(String path) {
        String downloadUrl = getPublicUrl(path);
        return super.download(downloadUrl);
    }

    @Override
    public String getPublicUrl(String path) {
        DownloadUrl url = new DownloadUrl(config.getEndpoint(), true, path);
        // 带有效期 24小时，可以自定义链接过期时间
        long expireInSeconds = 60 * 60 * 24 ;
        long deadline = System.currentTimeMillis()/1000 + expireInSeconds;
        String urlString = null;
        try {
            urlString = url.buildURL(auth, deadline);
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
        return urlString;
    }

    @Override
    public void delete(String path) {
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(config.getBucketName(), path);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }


}
