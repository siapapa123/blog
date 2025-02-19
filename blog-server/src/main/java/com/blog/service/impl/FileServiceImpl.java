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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.blog.constant.StorageConstants;

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

    /**
     * 生成基于日期的存储路径
     * @param baseDir 基础目录
     * @return 完整的存储路径
     */
    private String generateDatePath(String baseDir) {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern(StorageConstants.DATE_PATH_FORMAT));
        return baseDir + "/" + datePath;
    }

    /**
     * 获取带有重试机制的预签名URL
     */
    private String getPresignedUrlWithRetry(String objectName, int maxRetries) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(objectName)
                        .method(Method.GET)
                        .expiry(minioConfig.getUrlExpiry(), TimeUnit.HOURS)
                        .build());
            } catch (Exception e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    log.error("Failed to generate presigned URL after {} retries", maxRetries, e);
                    throw new RuntimeException("获取文件访问URL失败", e);
                }
                log.warn("Retry {} to generate presigned URL", retryCount);
                try {
                    Thread.sleep(1000L * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return null;
    }

    /**
     * 上传文件到MinIO（带重试机制）
     */
    private void putObjectWithRetry(PutObjectArgs args, int maxRetries) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                minioClient.putObject(args);
                return;
            } catch (Exception e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    log.error("Failed to upload file after {} retries", maxRetries, e);
                    throw new RuntimeException("文件上传失败", e);
                }
                log.warn("Retry {} to upload file", retryCount);
                try {
                    Thread.sleep(1000L * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    @Override
    public String uploadImage(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            Asserts.fail("上传图片不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            Asserts.fail("不支持的图片格式");
        }

        // 检查文件大小
        if (file.getSize() > getMaxImageSize(directory)) {
            Asserts.fail("图片大小超出限制");
        }

        return uploadFile(file, directory);
    }

    /**
     * 检查是否为有效的图片类型
     */
    private boolean isValidImageType(String contentType) {
        return contentType.startsWith("image/") && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp")
        );
    }

    /**
     * 获取不同目录的图片大小限制
     */
    private long getMaxImageSize(String directory) {
        // 1MB = 1024 * 1024
        if (directory.startsWith(StorageConstants.AVATAR_DIR)) {
            return 2 * 1024 * 1024L; // 头像最大2MB
        } else if (directory.startsWith(StorageConstants.ARTICLE_IMAGES_DIR)) {
            return 5 * 1024 * 1024L; // 文章图片最大5MB
        } else {
            return 10 * 1024 * 1024L; // 其他图片最大10MB
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String baseDir) {
        if (file == null || file.isEmpty()) {
            Asserts.fail("上传文件不能为空");
        }
        
        try {
            String fullPath = generateDatePath(baseDir);
            String filename = generateFilename(file.getOriginalFilename());
            String objectName = fullPath + "/" + filename;
            
            putObjectWithRetry(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build(),
                    3);

            return getPresignedUrlWithRetry(objectName, 3);
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
            String fullPath = generateDatePath(directory);
            String objectName = fullPath + "/" + filename;
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(contentBytes);
            putObjectWithRetry(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .stream(bais, contentBytes.length, -1)
                    .contentType("text/markdown")
                    .build(),
                    minioConfig.getMaxRetries());

            log.info("Content uploaded successfully: {}", objectName);
            return getPresignedUrlWithRetry(objectName, minioConfig.getMaxRetries());
        } catch (Exception e) {
            log.error("Error uploading content: {}", e.getMessage(), e);
            Asserts.fail("内容上传失败");
            return null;
        }
    }

    @Override
    public String getContent(String objectName) {
        try {
            GetObjectResponse response = getObjectWithRetry(GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build(),
                    minioConfig.getMaxRetries());

            try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = response.read(buffer)) != -1) {
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
            removeObjectWithRetry(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build(),
                    minioConfig.getMaxRetries());
        } catch (Exception e) {
            log.error("Error deleting file", e);
            Asserts.fail("文件删除失败");
        }
    }

    private String generateFilename(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        return UUID.randomUUID().toString() + (extension != null ? "." + extension : "");
    }

    /**
     * 获取对象（带重试机制）
     */
    private GetObjectResponse getObjectWithRetry(GetObjectArgs args, int maxRetries) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                return minioClient.getObject(args);
            } catch (Exception e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    log.error("Failed to get object after {} retries", maxRetries, e);
                    throw new RuntimeException("获取文件失败", e);
                }
                log.warn("Retry {} to get object", retryCount);
                try {
                    Thread.sleep(1000L * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        throw new RuntimeException("获取文件失败");
    }

    /**
     * 删除对象（带重试机制）
     */
    private void removeObjectWithRetry(RemoveObjectArgs args, int maxRetries) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                minioClient.removeObject(args);
                return;
            } catch (Exception e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    log.error("Failed to remove object after {} retries", maxRetries, e);
                    throw new RuntimeException("删除文件失败", e);
                }
                log.warn("Retry {} to remove object", retryCount);
                try {
                    Thread.sleep(1000L * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
} 