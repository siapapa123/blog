package com.blog.service;

import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;
import com.blog.dto.response.TokenResponse;
import com.blog.dto.response.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    UserResponse getCurrentUser();
} 