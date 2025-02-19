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
    NO_PERMISSION(2002, "没有操作权限"),
    
    /* 标签相关错误 */
    TAG_NOT_FOUND(4001, "标签不存在"),
    TAG_ALREADY_EXISTS(4002, "标签已存在"),
    
    // 评论相关错误
    COMMENT_NOT_FOUND(3001, "评论不存在"),
    COMMENT_CONTENT_EMPTY(3002, "评论内容不能为空"),
    COMMENT_CONTENT_TOO_LONG(3003, "评论内容不能超过500个字符"),
    
    /* 分类相关错误 */
    CATEGORY_NOT_FOUND(5001, "分类不存在"),
    CATEGORY_ALREADY_EXISTS(5002, "分类名称已存在"),
    CATEGORY_HAS_ARTICLES(5003, "分类下存在文章，无法删除");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
} 