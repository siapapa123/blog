package com.blog.repository;

import com.blog.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserRepository {
    User selectById(Long id);
    
    User selectByUsername(String username);
    
    int insert(User user);
    
    int updateById(User user);
    
    int deleteById(Long id);
    
    int existsByUsername(@Param("username") String username);
    
    int existsByEmail(@Param("email") String email);
} 