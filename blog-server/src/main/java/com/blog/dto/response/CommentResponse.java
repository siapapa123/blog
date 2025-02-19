package com.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "评论响应")
public class CommentResponse {
    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "评论用户")
    private UserResponse user;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子评论列表")
    private List<CommentResponse> children;
} 