package com.blog.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户实体
 * 对应数据库表：users
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 用户角色
     */
    private UserRole role;

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 