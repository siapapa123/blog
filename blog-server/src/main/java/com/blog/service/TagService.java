package com.blog.service;

import com.blog.dto.request.TagRequest;
import com.blog.dto.response.TagResponse;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface TagService {
    /**
     * 创建标签
     */
    TagResponse create(TagRequest request);

    /**
     * 更新标签
     */
    TagResponse update(Long id, TagRequest request);

    /**
     * 删除标签
     */
    void deleteById(Long id);

    /**
     * 获取标签详情
     */
    TagResponse getById(Long id);

    /**
     * 获取标签列表
     */
    PageInfo<TagResponse> getList(int pageNum, int pageSize, String keyword);

    /**
     * 获取所有标签
     */
    List<TagResponse> getAllTags();
} 