package com.blog.repository;

import com.blog.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryRepository {
    Category selectById(Long id);
    
    Category selectByName(String name);
    
    int insert(Category category);
    
    int update(Category category);
    
    int deleteById(Long id);
    
    List<Category> selectList(@Param("keyword") String keyword);
    
    List<Category> selectAll();
    
    int getArticleCount(Long categoryId);
    
    List<Category> selectByIds(@Param("ids") List<Long> ids);
} 