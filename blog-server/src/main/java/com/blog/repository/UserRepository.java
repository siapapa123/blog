package com.blog.repository;

import com.blog.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserRepository {
    User selectByPrimaryKey(Long id);
    
    User selectByUsername(String username);
    
    int insert(User user);
    
    int updateByPrimaryKeySelective(User user);
    
    int deleteByPrimaryKey(Long id);
    
    int existsByUsername(@Param("username") String username);
    
    int existsByEmail(@Param("email") String email);
} 