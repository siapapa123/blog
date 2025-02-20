package com.blog.service.impl;

import com.blog.common.response.ResultCode;
import com.blog.common.util.SecurityUtils;
import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;
import com.blog.dto.request.UserUpdateRequest;
import com.blog.dto.response.TokenResponse;
import com.blog.dto.response.UserResponse;
import com.blog.exception.Asserts;
import com.blog.model.User;
import com.blog.model.UserRole;
import com.blog.repository.UserRepository;
import com.blog.security.JwtTokenUtil;
import com.blog.service.FileService;
import com.blog.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final FileService fileService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.selectByUsername(request.getUsername()) != null) {
            Asserts.fail(ResultCode.USERNAME_ALREADY_EXISTS);
        }

        // 检查邮箱是否已被注册
        if (userRepository.selectByEmail(request.getEmail()) != null) {
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
    @CachePut(key = "#result.user.id")
    public TokenResponse login(LoginRequest request) {
        // 1. 根据用户名查找用户
        User user = userRepository.selectByUsername(request.getUsername());
        if (user == null) {
            Asserts.fail(ResultCode.FAILED);
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            Asserts.fail(ResultCode.FAILED);
        }

        // 3. 生成token
        String token = jwtTokenUtil.generateToken(user.getUsername());

        // 4. 构建响应（用户信息会被缓存）
        TokenResponse response = TokenResponse.builder()
                .token(token)
                .user(convertToVO(user))
                .build();

        return response;
    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.selectByUsername(username);
        return convertToVO(user);
    }

    @Override
    @Cacheable(key = "#id")
    public UserResponse getById(Long id) {
        User user = userRepository.selectById(id);
        if (user == null || user.getIsDeleted()) {
            Asserts.fail(ResultCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    @CachePut(key = "#id")
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        // 1. 检查用户是否存在
        User user = userRepository.selectById(id);
        if (user == null || user.getIsDeleted()) {
            Asserts.fail(ResultCode.USER_NOT_FOUND);
        }

        // 2. 检查用户名是否重复
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.selectByUsername(request.getUsername()) != null) {
                Asserts.fail(ResultCode.USERNAME_ALREADY_EXISTS);
            }
            user.setUsername(request.getUsername());
        }

        // 3. 检查邮箱是否重复
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.selectByEmail(request.getEmail()) != null) {
                Asserts.fail(ResultCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }

        // 4. 更新头像URL
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        // 5. 更新密码
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // 6. 保存更新
        if (userRepository.update(user) <= 0) {
            Asserts.fail("用户信息更新失败");
        }

        return convertToVO(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.selectById(id);
        if (user == null || user.getIsDeleted()) {
            Asserts.fail(ResultCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    @Override
    public PageInfo<UserResponse> getList(int pageNum, int pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userRepository.selectList(keyword);
        return new PageInfo<>(users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
    }

    @Override
    public boolean isCurrentUser(Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }

    private UserResponse convertToVO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }
} 