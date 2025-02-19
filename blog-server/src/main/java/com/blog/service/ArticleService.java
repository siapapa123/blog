package com.blog.service;

import com.blog.model.Article;
import com.blog.dto.request.ArticleCreateRequest;
import com.blog.dto.request.ArticleUpdateRequest;
import com.blog.dto.response.ArticleDetailResponse;
import com.github.pagehelper.PageInfo;

public interface ArticleService {
    /**
     * 创建文章
     * @param request 创建文章请求
     * @return 创建的文章
     */
    Article create(ArticleCreateRequest request);

    /**
     * 获取文章详情
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleDetailResponse getById(Long id);

    /**
     * 更新文章
     * @param id 文章ID
     * @param request 更新文章请求
     * @return 更新后的文章
     */
    Article update(Long id, ArticleUpdateRequest request);

    /**
     * 删除文章
     * @param id 文章ID
     */
    void deleteById(Long id);

    /**
     * 分页获取文章列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param categoryId 分类ID（可选）
     * @param keyword 关键词（可选）
     * @return 文章列表分页信息
     */
    PageInfo<ArticleDetailResponse> getList(int pageNum, int pageSize, Long categoryId, String keyword);

    /**
     * 更新文章状态
     * @param id 文章ID
     * @param status 状态
     */
    void updateStatus(Long id, String status);

    /**
     * 设置文章置顶状态
     * @param id 文章ID
     * @param isTop 是否置顶
     */
    void setTop(Long id, boolean isTop);

    /**
     * 增加文章浏览量
     * @param id 文章ID
     */
    void incrementViewCount(Long id);
} 