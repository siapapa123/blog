package com.blog.controller;

import com.blog.common.response.ApiResponse;
import com.blog.dto.request.TagRequest;
import com.blog.dto.response.TagResponse;
import com.blog.service.TagService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "标签管理")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    
    private final TagService tagService;

    @Operation(summary = "创建标签")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TagResponse> create(@Validated @RequestBody TagRequest request) {
        return ApiResponse.success(tagService.create(request));
    }

    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TagResponse> update(
            @PathVariable Long id,
            @Validated @RequestBody TagRequest request) {
        return ApiResponse.success(tagService.update(id, request));
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tagService.deleteById(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取标签详情")
    @GetMapping("/{id}")
    public ApiResponse<TagResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(tagService.getById(id));
    }

    @Operation(summary = "获取标签列表")
    @GetMapping
    public ApiResponse<PageInfo<TagResponse>> getList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return ApiResponse.success(tagService.getList(pageNum, pageSize, keyword));
    }

    @Operation(summary = "获取所有标签")
    @GetMapping("/all")
    public ApiResponse<List<TagResponse>> getAllTags() {
        return ApiResponse.success(tagService.getAllTags());
    }
} 