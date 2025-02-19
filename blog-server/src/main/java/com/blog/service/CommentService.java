package com.blog.service;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import java.util.List;

public interface CommentService {
    /**
     * 创建评论
     */
    CommentResponse create(CommentRequest request);

    /**
     * 删除评论
     */
    void deleteById(Long id);

    /**
     * 获取文章评论列表（树形结构）
     */
    List<CommentResponse> getArticleComments(Long articleId);

    /**
     * 获取用户的评论列表
     */
    List<CommentResponse> getUserComments(Long userId);

    /**
     * 检查当前用户是否为评论作者
     */
    boolean isCommentAuthor(Long commentId);
} 