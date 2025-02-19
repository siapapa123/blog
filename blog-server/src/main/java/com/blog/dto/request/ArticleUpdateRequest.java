package com.blog.dto.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class ArticleUpdateRequest {
    @Size(max = 200, message = "文章标题不能超过200个字符")
    private String title;

    private String content;

    @Size(max = 500, message = "文章摘要不能超过500个字符")
    private String summary;

    private MultipartFile cover;  // 封面图片
    
    private Long categoryId;
    
    private List<Long> tagIds;  // 文章标签ID列表
    
    private Boolean isTop;      // 是否置顶
} 