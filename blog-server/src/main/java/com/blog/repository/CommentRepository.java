package com.blog.repository;

import com.blog.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentRepository {
    int insert(Comment comment);
    
    Comment selectById(Long id);
    
    int deleteById(Long id);
    
    List<Comment> selectByArticleId(Long articleId);
    
    List<Comment> selectByParentId(Long parentId);
    
    List<Comment> selectByUserId(Long userId);
    
    int deleteByArticleId(Long articleId);
} 