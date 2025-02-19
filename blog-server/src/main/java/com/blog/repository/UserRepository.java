package com.blog.repository;

import com.blog.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserRepository {
    User selectById(Long id);
    
    User selectByUsername(String username);
    
    User selectByEmail(String email);
    
    int insert(User user);
    
    int update(User user);
    
    int deleteById(Long id);
    
    List<User> selectList(@Param("keyword") String keyword);
    
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    int updateRoles(@Param("id") Long id, @Param("roles") String[] roles);

    /**
     * 根据ID列表批量查询用户
     * @param ids 用户ID列表
     * @return 用户列表
     */
    List<User> selectByIds(@Param("ids") List<Long> ids);
} 