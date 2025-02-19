package com.blog.service.impl;

import com.blog.common.response.ResultCode;
import com.blog.common.util.SecurityUtils;
import com.blog.constant.StorageConstants;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.selectById(id);
        if (user == null || user.getIsDeleted()) {
            Asserts.fail(ResultCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = userRepository.selectById(id);
        if (user == null || user.getIsDeleted()) {
            Asserts.fail(ResultCode.USER_NOT_FOUND);
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            User existingUser = userRepository.selectByEmail(request.getEmail());
            if (existingUser != null && !existingUser.getId().equals(id)) {
                Asserts.fail(ResultCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarUrl = fileService.uploadImage(request.getAvatar(), StorageConstants.USER_AVATAR_DIR);
            user.setAvatarUrl(avatarUrl);
        }

        user.setUpdateTime(LocalDateTime.now());
        userRepository.update(user);
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
    @Transactional
    public void updateStatus(Long id, String status) {
        User user = userRepository.selectById(id);
        if (user == null || user.getIsDeleted()) {
            Asserts.fail(ResultCode.USER_NOT_FOUND);
        }
        userRepository.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void updateRoles(Long id, String[] roles) {
        User user = userRepository.selectById(id);
        if (user == null || user.getIsDeleted()) {
            Asserts.fail(ResultCode.USER_NOT_FOUND);
        }
        userRepository.updateRoles(id, roles);
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