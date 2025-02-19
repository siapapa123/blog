package com.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "用户信息更新请求")
public class UserUpdateRequest {
    @Schema(description = "用户名")
    @Size(max = 50, message = "用户名不能超过50个字符")
    private String username;
    
    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Schema(description = "头像文件")
    private MultipartFile avatar;
} 