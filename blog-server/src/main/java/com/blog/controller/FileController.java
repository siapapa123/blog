package com.blog.controller;

import com.blog.common.response.ApiResponse;
import com.blog.constant.StorageConstants;
import com.blog.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文章图片")
    @PostMapping(value = "/article/images", consumes = "multipart/form-data")
    public ApiResponse<String> uploadArticleImage(@RequestPart("file") MultipartFile file) {
        // 上传图片并返回URL
        String imageUrl = fileService.uploadImage(file, StorageConstants.ARTICLE_IMAGES_DIR);
        return ApiResponse.success(imageUrl);
    }

    @Operation(summary = "上传文章封面")
    @PostMapping(value = "/article/covers", consumes = "multipart/form-data")
    public ApiResponse<String> uploadArticleCover(
            @Parameter(description = "封面图片") @RequestPart("file") MultipartFile file) {
        return ApiResponse.success(fileService.uploadImage(file, StorageConstants.ARTICLE_IMAGES_DIR));
    }

    @Operation(summary = "上传用户头像")
    @PostMapping(value = "/user/avatars", consumes = "multipart/form-data")
    public ApiResponse<String> uploadUserAvatar(
            @Parameter(description = "头像图片") @RequestPart("file") MultipartFile file) {
        return ApiResponse.success(fileService.uploadImage(file, StorageConstants.AVATAR_DIR));
    }

    @Operation(summary = "删除文件")
    @DeleteMapping
    public ApiResponse<Void> deleteFile(
            @Parameter(description = "文件路径") @RequestPart String filePath) {
        fileService.deleteFile(filePath);
        return ApiResponse.success();
    }
} 