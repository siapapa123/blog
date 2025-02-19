package com.blog.repository;

import com.blog.model.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TagRepository {
    Tag selectById(Long id);
    
    Tag selectByName(String name);
    
    int insert(Tag tag);
    
    int update(Tag tag);
    
    int deleteById(Long id);
    
    List<Tag> selectList(@Param("keyword") String keyword);
    
    List<Tag> selectAll();
} 