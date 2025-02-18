package com.blog.common.response;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    
    // 用户相关错误
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_ALREADY_EXISTS(1002, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(1003, "邮箱已被注册"),
    PASSWORD_ERROR(1004, "密码错误"),
    
    // 文章相关错误
    ARTICLE_NOT_FOUND(2001, "文章不存在"),
    NO_PERMISSION(2002, "没有操作权限");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
} 