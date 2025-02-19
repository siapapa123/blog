package com.blog.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    /**
     * 评论ID
     */
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 