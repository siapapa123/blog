package com.blog.constant;

public class StorageConstants {
    // 基础目录
    public static final String BASE_DIR = "blog";
    
    // 文章相关目录
    public static final String ARTICLE_DIR = BASE_DIR + "/articles";
    public static final String ARTICLE_CONTENT_DIR = ARTICLE_DIR + "/content";
    public static final String ARTICLE_IMAGES_DIR = ARTICLE_DIR + "/images";
    
    // 用户相关目录
    public static final String USER_DIR = BASE_DIR + "/users";
    public static final String AVATAR_DIR = USER_DIR + "/avatars";
    
    // 按时间组织的目录格式
    public static final String DATE_PATH_FORMAT = "yyyy/MM/dd";

    private StorageConstants() {}
} 