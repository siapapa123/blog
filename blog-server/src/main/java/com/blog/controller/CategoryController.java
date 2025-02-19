package com.blog.controller;

import com.blog.common.response.ApiResponse;
import com.blog.dto.request.CategoryRequest;
import com.blog.dto.response.CategoryResponse;
import com.blog.service.CategoryService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分类管理")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "创建分类")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> create(@Validated @RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.create(request));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> update(
            @PathVariable Long id,
            @Validated @RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.update(id, request));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(categoryService.getById(id));
    }

    @Operation(summary = "获取分类列表")
    @GetMapping
    public ApiResponse<PageInfo<CategoryResponse>> getList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return ApiResponse.success(categoryService.getList(pageNum, pageSize, keyword));
    }

    @Operation(summary = "获取所有分类")
    @GetMapping("/all")
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }
} 