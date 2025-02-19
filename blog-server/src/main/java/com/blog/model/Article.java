package com.blog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private Long id;
    private String title;
    private String contentUrl;  // MinIO中的文件路径
    private String summary;
    private String coverUrl;    // 封面图片路径
    private Long authorId;
    private Long categoryId;
    private ArticleStatus status;
    private Integer viewCount;
    private Boolean isTop;
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 