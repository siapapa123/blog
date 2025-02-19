package com.blog.dto.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class ArticleCreateRequest {
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题不能超过200个字符")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    @Size(max = 500, message = "文章摘要不能超过500个字符")
    private String summary;

    private MultipartFile cover;  // 封面图片
    
    private Long categoryId;
} 