package com.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Schema(description = "分类请求")
public class CategoryRequest {
    @Schema(description = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称不能超过50个字符")
    private String name;
    
    @Schema(description = "分类描述")
    @Size(max = 200, message = "分类描述不能超过200个字符")
    private String description;
    
    @Schema(description = "排序")
    private Integer sort;
} 