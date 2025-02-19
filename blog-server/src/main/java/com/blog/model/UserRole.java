package com.blog.model;

/**
 * 用户角色枚举
 */
public enum UserRole {
    /**
     * 管理员
     */
    ADMIN,

    /**
     * 普通用户
     */
    USER,

    /**
     * 访客
     */
    GUEST;

    /**
     * 获取角色名称
     */
    public String getName() {
        return this.name();
    }

    /**
     * 从字符串转换为枚举
     */
    public static UserRole fromString(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER; // 默认为普通用户
        }
    }
} 