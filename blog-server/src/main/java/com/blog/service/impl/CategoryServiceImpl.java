package com.blog.service.impl;

import com.blog.common.response.ResultCode;
import com.blog.dto.request.CategoryRequest;
import com.blog.dto.response.CategoryResponse;
import com.blog.exception.Asserts;
import com.blog.model.Category;
import com.blog.repository.CategoryRepository;
import com.blog.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        // 检查分类名是否已存在
        if (categoryRepository.selectByName(request.getName()) != null) {
            Asserts.fail(ResultCode.CATEGORY_ALREADY_EXISTS);
        }

        // 创建新分类
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sort(request.getSort() != null ? request.getSort() : 0)
                .createTime(LocalDateTime.now())
                .build();

        categoryRepository.insert(category);
        return convertToVO(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        // 检查分类是否存在
        Category category = categoryRepository.selectById(id);
        if (category == null) {
            Asserts.fail(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 检查新名称是否与其他分类重复
        Category existingCategory = categoryRepository.selectByName(request.getName());
        if (existingCategory != null && !existingCategory.getId().equals(id)) {
            Asserts.fail(ResultCode.CATEGORY_ALREADY_EXISTS);
        }

        // 更新分类
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getSort() != null) {
            category.setSort(request.getSort());
        }

        categoryRepository.update(category);
        return convertToVO(category);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // 检查分类是否存在
        Category category = categoryRepository.selectById(id);
        if (category == null) {
            Asserts.fail(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 检查分类下是否有文章
        int articleCount = categoryRepository.getArticleCount(id);
        if (articleCount > 0) {
            Asserts.fail(ResultCode.CATEGORY_HAS_ARTICLES);
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.selectById(id);
        if (category == null) {
            Asserts.fail(ResultCode.CATEGORY_NOT_FOUND);
        }
        return convertToVO(category);
    }

    @Override
    public PageInfo<CategoryResponse> getList(int pageNum, int pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<Category> categories = categoryRepository.selectList(keyword);
        return new PageInfo<>(categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.selectAll().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private CategoryResponse convertToVO(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .sort(category.getSort())
                .articleCount(categoryRepository.getArticleCount(category.getId()))
                .createTime(category.getCreateTime())
                .build();
    }
} 