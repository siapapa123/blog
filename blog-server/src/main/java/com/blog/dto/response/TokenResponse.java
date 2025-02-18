package com.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    @Schema(description = "JWT令牌")
    private String token;

    @Schema(description = "用户信息")
    private UserResponse user;
} 