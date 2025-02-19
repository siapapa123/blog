package com.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
@Schema(description = "标签响应")
public class TagResponse {
    @Schema(description = "标签ID")
    private Long id;

    @Schema(description = "标签名称")
    private String name;
} 