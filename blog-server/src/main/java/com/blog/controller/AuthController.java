package com.blog.controller;

import com.blog.common.response.ApiResponse;
import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;
import com.blog.dto.response.TokenResponse;
import com.blog.dto.response.UserResponse;
import com.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Validated @RequestBody RegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Validated @RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public ApiResponse<UserResponse> getCurrentUser() {
        return ApiResponse.success(userService.getCurrentUser());
    }
} 