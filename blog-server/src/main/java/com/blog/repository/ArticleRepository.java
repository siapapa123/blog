package com.blog.repository;

import com.blog.model.Article;
import com.blog.dto.response.TagResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ArticleRepository {
    int insert(Article article);
    
    Article selectById(Long id);
    
    int updateById(Article article);
    
    int deleteById(Long id);
    
    List<Article> selectList(@Param("categoryId") Long categoryId, 
                           @Param("keyword") String keyword);
    
    List<TagResponse> selectArticleTags(Long articleId);
    
    int updateViewCount(@Param("id") Long id);
    
    int updateTop(@Param("id") Long id, @Param("isTop") Boolean isTop);
    
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 删除文章的所有标签关联
     * @param articleId 文章ID
     * @return 影响行数
     */
    int deleteArticleTags(Long articleId);

    /**
     * 批量插入文章标签关联
     * @param articleId 文章ID
     * @param tagIds 标签ID列表
     * @return 影响行数
     */
    int insertArticleTags(@Param("articleId") Long articleId, @Param("tagIds") List<Long> tagIds);

    List<Article> selectHotArticles(@Param("limit") int limit);
    
    List<Article> selectRecommendedArticles(
            @Param("articleId") Long articleId,
            @Param("categoryId") Long categoryId,
            @Param("tagIds") List<Long> tagIds,
            @Param("limit") int limit
    );
    
    List<Article> selectByAuthorId(@Param("authorId") Long authorId);
    
    List<Article> selectListWithParams(
            @Param("categoryId") Long categoryId,
            @Param("tagId") Long tagId,
            @Param("keyword") String keyword,
            @Param("sortField") String sortField,
            @Param("isDesc") boolean isDesc
    );
} 