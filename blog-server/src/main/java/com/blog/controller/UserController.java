package com.blog.controller;

import com.blog.common.response.ApiResponse;
import com.blog.dto.request.UserUpdateRequest;
import com.blog.dto.response.UserResponse;
import com.blog.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取用户信息")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(userService.getById(id));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    @PreAuthorize("@userService.isCurrentUser(#id) or hasRole('ADMIN')")
    public ApiResponse<UserResponse> update(
            @PathVariable Long id,
            @Validated @RequestBody UserUpdateRequest request) {
        return ApiResponse.success(userService.update(id, request));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取用户列表")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageInfo<UserResponse>> getList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return ApiResponse.success(userService.getList(pageNum, pageSize, keyword));
    }
} 