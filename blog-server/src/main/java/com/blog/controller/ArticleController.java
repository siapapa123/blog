package com.blog.controller;

import com.blog.common.response.ApiResponse;
import com.blog.dto.request.ArticleCreateRequest;
import com.blog.dto.request.ArticleUpdateRequest;
import com.blog.dto.response.ArticleDetailResponse;
import com.blog.model.Article;
import com.blog.service.ArticleService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "文章管理")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "创建文章")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Article> create(@Validated @RequestBody ArticleCreateRequest request) {
        return ApiResponse.success(articleService.create(request));
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/{id}")
    public ApiResponse<ArticleDetailResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(articleService.getById(id));
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    @PreAuthorize("@articleService.isArticleAuthor(#id)")
    public ApiResponse<Article> update(
            @PathVariable Long id,
            @Validated @RequestBody ArticleUpdateRequest request) {
        return ApiResponse.success(articleService.update(id, request));
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    @PreAuthorize("@articleService.isArticleAuthor(#id)")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        articleService.deleteById(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取文章列表")
    @GetMapping
    public ApiResponse<PageInfo<ArticleDetailResponse>> getList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "标签ID") @RequestParam(required = false) Long tagId,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortField,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDirection) {
        return ApiResponse.success(articleService.getList(pageNum, pageSize, categoryId, tagId, keyword, sortField, sortDirection));
    }

    @Operation(summary = "获取热门文章")
    @GetMapping("/hot")
    public ApiResponse<List<ArticleDetailResponse>> getHotArticles(
            @Parameter(description = "获取数量") @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(articleService.getHotArticles(limit));
    }

    @Operation(summary = "获取推荐文章")
    @GetMapping("/recommended")
    public ApiResponse<List<ArticleDetailResponse>> getRecommendedArticles(
            @Parameter(description = "文章ID") @PathVariable Long articleId,
            @Parameter(description = "获取数量") @RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success(articleService.getRecommendedArticles(articleId, limit));
    }

    @Operation(summary = "更新文章状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam String status) {
        articleService.updateStatus(id, status);
        return ApiResponse.success();
    }

    @Operation(summary = "设置文章置顶")
    @PutMapping("/{id}/top")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> setTop(
            @PathVariable Long id,
            @Parameter(description = "是否置顶") @RequestParam boolean isTop) {
        articleService.setTop(id, isTop);
        return ApiResponse.success();
    }

    @Operation(summary = "增加文章浏览量")
    @PostMapping("/{id}/view")
    public ApiResponse<Void> incrementViewCount(@PathVariable Long id) {
        articleService.incrementViewCount(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取用户的文章列表")
    @GetMapping("/user/{userId}")
    public ApiResponse<PageInfo<ArticleDetailResponse>> getUserArticles(
            @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(articleService.getUserArticles(userId, pageNum, pageSize));
    }
} 