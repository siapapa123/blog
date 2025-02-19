package com.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Schema(description = "用户信息更新请求")
public class UserUpdateRequest {
    @Schema(description = "用户名")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    private String username;
    
    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Schema(description = "头像URL")
    private String avatarUrl;
    
    @Schema(description = "密码")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
} 