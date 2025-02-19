package com.blog.service.impl;

import com.blog.common.response.ResultCode;
import com.blog.dto.request.TagRequest;
import com.blog.dto.response.TagResponse;
import com.blog.exception.Asserts;
import com.blog.model.Tag;
import com.blog.repository.TagRepository;
import com.blog.service.TagService;
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
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public TagResponse create(TagRequest request) {
        // 检查标签名是否已存在
        if (tagRepository.selectByName(request.getName()) != null) {
            Asserts.fail(ResultCode.TAG_ALREADY_EXISTS);
        }

        // 创建新标签
        Tag tag = Tag.builder()
                .name(request.getName())
                .createTime(LocalDateTime.now())
                .build();

        tagRepository.insert(tag);
        return convertToVO(tag);
    }

    @Override
    @Transactional
    public TagResponse update(Long id, TagRequest request) {
        // 检查标签是否存在
        Tag tag = tagRepository.selectById(id);
        if (tag == null) {
            Asserts.fail(ResultCode.TAG_NOT_FOUND);
        }

        // 检查新名称是否与其他标签重复
        Tag existingTag = tagRepository.selectByName(request.getName());
        if (existingTag != null && !existingTag.getId().equals(id)) {
            Asserts.fail(ResultCode.TAG_ALREADY_EXISTS);
        }

        // 更新标签
        tag.setName(request.getName());
        tagRepository.update(tag);
        
        return convertToVO(tag);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // 检查标签是否存在
        Tag tag = tagRepository.selectById(id);
        if (tag == null) {
            Asserts.fail(ResultCode.TAG_NOT_FOUND);
        }

        tagRepository.deleteById(id);
    }

    @Override
    public TagResponse getById(Long id) {
        Tag tag = tagRepository.selectById(id);
        if (tag == null) {
            Asserts.fail(ResultCode.TAG_NOT_FOUND);
        }
        return convertToVO(tag);
    }

    @Override
    public PageInfo<TagResponse> getList(int pageNum, int pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<Tag> tags = tagRepository.selectList(keyword);
        return new PageInfo<>(tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
    }

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.selectAll().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private TagResponse convertToVO(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
} 