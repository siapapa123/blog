package com.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Schema(description = "文章标签请求")
public class TagRequest {
    @Schema(description = "标签名称")
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 20, message = "标签名称不能超过20个字符")
    private String name;
} 