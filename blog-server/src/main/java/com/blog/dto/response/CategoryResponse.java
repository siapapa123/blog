package com.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "分类响应")
public class CategoryResponse {
    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "文章数量")
    private Integer articleCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
} 