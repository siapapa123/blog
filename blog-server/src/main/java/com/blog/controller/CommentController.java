package com.blog.controller;

import com.blog.common.response.ApiResponse;
import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论管理")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "创建评论")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CommentResponse> create(@Validated @RequestBody CommentRequest request) {
        return ApiResponse.success(commentService.create(request));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        commentService.deleteById(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取文章评论列表")
    @GetMapping("/article/{articleId}")
    public ApiResponse<List<CommentResponse>> getArticleComments(
            @Parameter(description = "文章ID") @PathVariable Long articleId) {
        return ApiResponse.success(commentService.getArticleComments(articleId));
    }

    @Operation(summary = "获取用户评论列表")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<CommentResponse>> getUserComments(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return ApiResponse.success(commentService.getUserComments(userId));
    }
} 