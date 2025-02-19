package com.blog.service;

import com.blog.dto.request.CategoryRequest;
import com.blog.dto.response.CategoryResponse;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface CategoryService {
    /**
     * 创建分类
     */
    CategoryResponse create(CategoryRequest request);

    /**
     * 更新分类
     */
    CategoryResponse update(Long id, CategoryRequest request);

    /**
     * 删除分类
     */
    void deleteById(Long id);

    /**
     * 获取分类详情
     */
    CategoryResponse getById(Long id);

    /**
     * 获取分类列表（分页）
     */
    PageInfo<CategoryResponse> getList(int pageNum, int pageSize, String keyword);

    /**
     * 获取所有分类（不分页）
     */
    List<CategoryResponse> getAllCategories();
} 