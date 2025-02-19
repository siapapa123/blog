package com.blog.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 上传图片
     * @param file 图片文件
     * @param directory 存储目录
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file, String directory);

    /**
     * 上传文件
     * @param file 文件
     * @param directory 存储目录
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String directory);

    /**
     * 上传文本内容
     * @param content 文本内容
     * @param directory 存储目录
     * @param filename 文件名
     * @return 文件访问URL
     */
    String uploadContent(String content, String directory, String filename);

    /**
     * 获取文件内容
     * @param objectName 对象名称
     * @return 文件内容
     */
    String getContent(String objectName);

    /**
     * 删除文件
     * @param objectName 对象名称
     */
    void deleteFile(String objectName);
} 