package com.blog.service;

import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;
import com.blog.dto.request.UserUpdateRequest;
import com.blog.dto.response.TokenResponse;
import com.blog.dto.response.UserResponse;
import com.github.pagehelper.PageInfo;

public interface UserService {
    UserResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    UserResponse getCurrentUser();
    UserResponse getById(Long id);
    UserResponse update(Long id, UserUpdateRequest request);
    void deleteById(Long id);
    PageInfo<UserResponse> getList(int pageNum, int pageSize, String keyword);
    boolean isCurrentUser(Long userId);
} 