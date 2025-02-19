package com.blog.dto.response;

import com.blog.model.ArticleStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ArticleDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverUrl;
    private Long authorId;
    private String authorName;  // 作者名称
    private Long categoryId;
    private String categoryName;  // 分类名称
    private ArticleStatus status;
    private Integer viewCount;
    private Boolean isTop;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    private List<TagResponse> tags;  // 文章标签列表
} 