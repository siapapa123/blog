package com.blog.service.impl;

import com.blog.config.MinioConfig;
import com.blog.service.FileService;
import com.blog.exception.Asserts;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @PostConstruct
    public void init() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .build());
                log.info("Created bucket: {}", minioConfig.getBucketName());
            }
        } catch (Exception e) {
            log.error("Error initializing MinIO", e);
            Asserts.fail("MinIO初始化失败");
        }
    }

    @Override
    public String uploadImage(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            Asserts.fail("上传图片不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            Asserts.fail("只支持上传图片文件");
        }

        return uploadFile(file, directory);
    }

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            Asserts.fail("上传文件不能为空");
        }
        
        try {
            String filename = generateFilename(file.getOriginalFilename());
            String objectName = directory + "/" + filename;
            
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return getFileUrl(objectName);
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            Asserts.fail("文件上传失败");
            return null;
        }
    }

    @Override
    public String uploadContent(String content, String directory, String filename) {
        if (content == null || content.trim().isEmpty()) {
            Asserts.fail("上传内容不能为空");
        }
        
        try {
            String objectName = directory + "/" + filename;
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(contentBytes);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .stream(bais, contentBytes.length, -1)
                    .contentType("text/markdown")
                    .build());

            log.info("Content uploaded successfully: {}", objectName);
            return getFileUrl(objectName);
        } catch (Exception e) {
            log.error("Error uploading content: {}", e.getMessage(), e);
            Asserts.fail("内容上传失败");
            return null;
        }
    }

    @Override
    public String getContent(String objectName) {
        try {
            try (InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build())) {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                return result.toString(StandardCharsets.UTF_8.name());
            }
        } catch (Exception e) {
            log.error("Error getting content: {}", e.getMessage(), e);
            Asserts.fail("获取内容失败");
            return null;
        }
    }

    @Override
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("Error deleting file", e);
            Asserts.fail("文件删除失败");
        }
    }

    private String generateFilename(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        return UUID.randomUUID().toString() + (extension != null ? "." + extension : "");
    }

    private String getFileUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .method(Method.GET)
                    .expiry(7, TimeUnit.DAYS)
                    .build());
        } catch (Exception e) {
            log.error("Error generating URL", e);
            Asserts.fail("URL生成失败");
            return null;
        }
    }
} 