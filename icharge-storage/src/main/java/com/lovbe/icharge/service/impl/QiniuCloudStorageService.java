package com.lovbe.icharge.service.impl;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.config.OssStorageConfig;
import com.lovbe.icharge.service.OssStorageService;
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
        try {
            String token = auth.uploadToken(config.getBucketName());
            Response res = uploadManager.put(bytes, path, token);
            if (!res.isOK()) {
                throw new ServiceException("上传七牛出错：" + res);
            }
        } catch (Exception e) {
            throw new ServiceException("上传文件失败，请核对七牛配置信息");
        }
        return StrUtil.addPrefixIfNot(path, "/");
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
//        long expireInSeconds = 60 * 60 * 24 ;
        long expireInSeconds = 60;
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
