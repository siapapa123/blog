-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog;

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色：USER/ADMIN',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 文章表
CREATE TABLE IF NOT EXISTS articles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    content_url VARCHAR(500) NOT NULL COMMENT '文章内容的MinIO存储路径',
    summary VARCHAR(500) COMMENT '文章摘要',
    cover_url VARCHAR(500) COMMENT '封面图片的MinIO存储路径',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    category_id BIGINT COMMENT '分类ID',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT/PUBLISHED',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_author (author_id),
    INDEX idx_category (category_id),
    INDEX idx_create_time (create_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 文章标签关联表
CREATE TABLE IF NOT EXISTS article_tags (
    article_id BIGINT NOT NULL COMMENT '文章ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (article_id, tag_id),
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL COMMENT '评论内容',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    parent_id BIGINT COMMENT '父评论ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_article (article_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- 插入初始管理员用户
INSERT INTO users (username, password, email, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@blog.com', 'ADMIN');

-- 添加一些默认分类
INSERT INTO categories (name, description, sort) VALUES 
('技术', '技术相关文章', 1),
('生活', '生活随笔', 2);

SET FOREIGN_KEY_CHECKS = 1;

-- 可以在这里添加其他表的创建语句 