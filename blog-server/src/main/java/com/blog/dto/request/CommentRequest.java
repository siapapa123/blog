package com.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Schema(description = "评论请求")
public class CommentRequest {
    @Schema(description = "文章ID")
    @NotNull(message = "文章ID不能为空")
    private Long articleId;
    
    @Schema(description = "评论内容")
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500个字符")
    private String content;
    
    @Schema(description = "父评论ID")
    private Long parentId;
} 