package com.lovbe.icharge.service.impl;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.HashMultimap;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.config.OssStorageConfig;
import com.lovbe.icharge.service.OssStorageService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.*;
import com.qiniu.util.Auth;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.ListPartsResult;
import io.minio.messages.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @description: 七牛云存储
 * @author: Lvhl
 * @date: 2024/5/9 13:09
 */
@Slf4j
public class MinioStorageService extends OssStorageService {
    private String bucketName;
    private MinioClient client;

    public MinioStorageService(OssStorageConfig config) {
        this.config = config;
        this.bucketName = config.getBucketName();
        client = MinioClient.builder()
                .endpoint(config.getEndpoint())
                .credentials(config.getAccessKey(), config.getSecretKey())
                .build();
        init(client);
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            this.client.putObject(PutObjectArgs.builder()
                    .bucket(this.bucketName)
                    .object(path)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType("application/octet-stream").build());
        } catch (Exception e) {
            log.error("[文件上传]--minio上传文件失败，errorInfo：{}", e.toString());
        }
        return new StringBuilder("/")
                .append(this.bucketName)
                .append("/")
                .append(path)
                .toString();
    }

    @Override
    public InputStream downloadFile(String path) {
        String downloadUrl = getPublicUrl(path);
        return super.download(downloadUrl);
    }

    @Override
    public String getPublicUrl(String path) {
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String fileName = path.replaceFirst(this.bucketName, "");
        if (fileName.startsWith("/")) {
            fileName = fileName.replaceFirst("/", "");
        }
        String previewUrl = null;
        try {
            previewUrl = this.client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("[文件预览]--mini预览文件失败，errorInfo：{}", e.toString());
        }
        return previewUrl;
    }

    @Override
    public void delete(String path) {
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        String fileName = path.replaceFirst(this.bucketName, "");
        if (fileName.startsWith("/")) {
            fileName = fileName.replaceFirst("/", "");
        }

        try {
            this.client.removeObject(RemoveObjectArgs.builder()
                    .bucket(this.bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("[文件删除]--minio删除文件失败，errorInfo：{}", e.toString());
        }
    }

    public void init(MinioClient minioClient) {
        boolean isExist = false;
        try {
            isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(this.bucketName).build());
            if (!isExist) {
                // 新创建的桶默认为私有
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(this.bucketName).build());
            }
            //设置桶策略Access Policy为public
            String policy = "{\"Version\":\"2012-10-17\"," +
                    "\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":" +
                    "{\"AWS\":[\"*\"]},\"Action\":[\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"," +
                    "\"s3:GetBucketLocation\"],\"Resource\":[\"arn:aws:s3:::" + this.bucketName +
                    "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:PutObject\"," +
                    "\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\"]," +
                    "\"Resource\":[\"arn:aws:s3:::" + this.bucketName + "/*\"]}]}";
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(this.bucketName).config(policy).build());
        } catch (Exception e) {
            log.error(e.toString());
            throw new ServiceException("Bucket连接失败.");
        }
    }

}
