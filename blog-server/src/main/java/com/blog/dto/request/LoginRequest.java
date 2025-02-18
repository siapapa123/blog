package com.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @Schema(description = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
} 