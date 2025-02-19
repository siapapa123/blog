package com.blog.controller;

import com.blog.common.response.ApiResponse;
import com.blog.dto.request.ArticleCreateRequest;
import com.blog.dto.request.ArticleUpdateRequest;
import com.blog.dto.response.ArticleDetailResponse;
import com.blog.model.Article;
import com.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageInfo;

@Tag(name = "文章管理")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "创建文章")
    @PostMapping
    public ApiResponse<Article> create(@Validated @RequestBody ArticleCreateRequest request) {
        return ApiResponse.success(articleService.create(request));
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/{id}")
    public ApiResponse<ArticleDetailResponse> getById(
            @Parameter(description = "文章ID") @PathVariable Long id) {
        return ApiResponse.success(articleService.getById(id));
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    public ApiResponse<Article> update(
            @Parameter(description = "文章ID") @PathVariable Long id,
            @Validated @RequestBody ArticleUpdateRequest request) {
        return ApiResponse.success(articleService.update(id, request));
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "文章ID") @PathVariable Long id) {
        articleService.deleteById(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取文章列表")
    @GetMapping
    public ApiResponse<PageInfo<ArticleDetailResponse>> getList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return ApiResponse.success(articleService.getList(pageNum, pageSize, categoryId, keyword));
    }

    @Operation(summary = "更新文章状态")
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @Parameter(description = "文章ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam String status) {
        articleService.updateStatus(id, status);
        return ApiResponse.success();
    }

    @Operation(summary = "设置文章置顶")
    @PutMapping("/{id}/top")
    public ApiResponse<Void> setTop(
            @Parameter(description = "文章ID") @PathVariable Long id,
            @Parameter(description = "是否置顶") @RequestParam boolean isTop) {
        articleService.setTop(id, isTop);
        return ApiResponse.success();
    }

    @Operation(summary = "增加文章浏览量")
    @PostMapping("/{id}/view")
    public ApiResponse<Void> incrementViewCount(
            @Parameter(description = "文章ID") @PathVariable Long id) {
        articleService.incrementViewCount(id);
        return ApiResponse.success();
    }
} 