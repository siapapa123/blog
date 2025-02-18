package com.blog.service.impl;

import com.blog.common.response.ResultCode;
import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;
import com.blog.dto.response.TokenResponse;
import com.blog.dto.response.UserResponse;
import com.blog.exception.Asserts;
import com.blog.model.User;
import com.blog.model.UserRole;
import com.blog.repository.UserRepository;
import com.blog.security.JwtTokenUtil;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername()) > 0) {
            Asserts.fail(ResultCode.USERNAME_ALREADY_EXISTS);
        }

        // 检查邮箱是否已被注册
        if (userRepository.existsByEmail(request.getEmail()) > 0) {
            Asserts.fail(ResultCode.EMAIL_ALREADY_EXISTS);
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(UserRole.USER);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userRepository.insert(user);
        
        return convertToVO(user);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        // 进行身份验证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 生成JWT token
        String token = jwtTokenUtil.generateToken(authentication);
        
        // 获取用户信息
        User user = userRepository.selectByUsername(request.getUsername());
        
        return TokenResponse.builder()
                .token(token)
                .user(convertToVO(user))
                .build();
    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.selectByUsername(username);
        return convertToVO(user);
    }

    private UserResponse convertToVO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createTime(user.getCreateTime())
                .build();
    }
} 