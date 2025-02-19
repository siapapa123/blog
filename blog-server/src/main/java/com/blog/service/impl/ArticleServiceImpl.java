package com.blog.service.impl;

import com.blog.constant.StorageConstants;
import com.blog.common.util.SecurityUtils;
import com.blog.model.Article;
import com.blog.model.ArticleStatus;
import com.blog.dto.request.ArticleCreateRequest;
import com.blog.dto.request.ArticleUpdateRequest;
import com.blog.dto.response.ArticleDetailResponse;
import com.blog.dto.response.TagResponse;
import com.blog.exception.Asserts;
import com.blog.service.ArticleService;
import com.blog.service.StorageService;
import com.blog.repository.ArticleRepository;
import com.blog.repository.UserRepository;
import com.blog.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    
    private final StorageService storageService;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public Article create(ArticleCreateRequest request) {
        // 1. 保存文章内容到MinIO
        String contentUrl = storageService.uploadContent(
            request.getContent(),
            StorageConstants.ARTICLE_CONTENT_DIR,
            UUID.randomUUID().toString() + ".md"
        );
        
        // 2. 如果有封面图片，上传到MinIO
        String coverUrl = null;
        if (request.getCover() != null && !request.getCover().isEmpty()) {
            coverUrl = storageService.uploadFile(
                request.getCover(),
                StorageConstants.ARTICLE_IMAGES_DIR
            );
        }
        
        // 3. 保存文章信息到数据库
        Article article = Article.builder()
                .title(request.getTitle())
                .contentUrl(contentUrl)
                .coverUrl(coverUrl)
                .summary(request.getSummary())
                .authorId(SecurityUtils.getCurrentUserId())
                .categoryId(request.getCategoryId())
                .status(ArticleStatus.DRAFT)
                .viewCount(0)
                .isTop(false)
                .isDeleted(false)
                .build();
                
        if (articleRepository.insert(article) <= 0) {
            Asserts.fail("文章创建失败");
        }
        return article;
    }
    
    @Override
    public ArticleDetailResponse getById(Long id) {
        // 1. 获取文章基本信息
        Article article = articleRepository.selectById(id);
        if (article == null) {
            Asserts.fail("文章不存在");
        }
        
        // 2. 获取文章内容
        String content = storageService.getContent(getObjectNameFromUrl(article.getContentUrl()));
        
        return ArticleDetailResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(content)
                .summary(article.getSummary())
                .coverUrl(article.getCoverUrl())
                .authorId(article.getAuthorId())
                .authorName(getUserName(article.getAuthorId()))
                .categoryId(article.getCategoryId())
                .categoryName(getCategoryName(article.getCategoryId()))
                .status(article.getStatus())
                .viewCount(article.getViewCount())
                .isTop(article.getIsTop())
                .createTime(article.getCreateTime())
                .updateTime(article.getUpdateTime())
                .tags(getArticleTags(article.getId()))
                .build();
    }
    
    @Override
    @Transactional
    public Article update(Long id, ArticleUpdateRequest request) {
        // 1. 检查文章是否存在
        Article article = articleRepository.selectById(id);
        if (article == null) {
            Asserts.fail("文章不存在");
        }
        
        // 2. 如果有新的内容，更新到MinIO
        if (request.getContent() != null) {
            String contentUrl = storageService.uploadContent(
                request.getContent(),
                StorageConstants.ARTICLE_CONTENT_DIR,
                UUID.randomUUID().toString() + ".md"
            );
            article.setContentUrl(contentUrl);
        }
        
        // 3. 如果有新的封面图片，更新到MinIO
        if (request.getCover() != null && !request.getCover().isEmpty()) {
            String coverUrl = storageService.uploadFile(
                request.getCover(),
                StorageConstants.ARTICLE_IMAGES_DIR
            );
            article.setCoverUrl(coverUrl);
        }
        
        // 4. 更新其他字段
        if (request.getTitle() != null) {
            article.setTitle(request.getTitle());
        }
        if (request.getSummary() != null) {
            article.setSummary(request.getSummary());
        }
        if (request.getCategoryId() != null) {
            article.setCategoryId(request.getCategoryId());
        }
        if (request.getIsTop() != null) {
            article.setIsTop(request.getIsTop());
        }
        
        // 5. 更新到数据库
        if (articleRepository.updateById(article) <= 0) {
            Asserts.fail("文章更新失败");
        }
        
        // 6. 如果有标签更新，处理标签关联
        if (request.getTagIds() != null) {
            updateArticleTags(id, request.getTagIds());
        }
        
        return article;
    }
    
    @Override
    @Transactional
    public void deleteById(Long id) {
        if (articleRepository.deleteById(id) <= 0) {
            Asserts.fail("文章删除失败");
        }
    }
    
    @Override
    public PageInfo<ArticleDetailResponse> getList(int pageNum, int pageSize, Long categoryId, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleRepository.selectList(categoryId, keyword);
        return new PageInfo<>(articles.stream()
                .map(this::convertToDetailResponse)
                .collect(Collectors.toList()));
    }
    
    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        if (articleRepository.updateStatus(id, status) <= 0) {
            Asserts.fail("更新文章状态失败");
        }
    }
    
    @Override
    @Transactional
    public void setTop(Long id, boolean isTop) {
        if (articleRepository.updateTop(id, isTop) <= 0) {
            Asserts.fail("设置文章置顶状态失败");
        }
    }
    
    @Override
    public void incrementViewCount(Long id) {
        articleRepository.updateViewCount(id);
    }
    
    private ArticleDetailResponse convertToDetailResponse(Article article) {
        return ArticleDetailResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .summary(article.getSummary())
                .coverUrl(article.getCoverUrl())
                .authorId(article.getAuthorId())
                .authorName(getUserName(article.getAuthorId()))
                .categoryId(article.getCategoryId())
                .categoryName(getCategoryName(article.getCategoryId()))
                .status(article.getStatus())
                .viewCount(article.getViewCount())
                .isTop(article.getIsTop())
                .createTime(article.getCreateTime())
                .updateTime(article.getUpdateTime())
                .tags(getArticleTags(article.getId()))
                .build();
    }
    
    private String getUserName(Long userId) {
        User user = userRepository.selectById(userId);
        return user != null ? user.getUsername() : null;
    }
    
    private String getCategoryName(Long categoryId) {
        // TODO: 实现分类查询
        return null;
    }
    
    private List<TagResponse> getArticleTags(Long articleId) {
        return articleRepository.selectArticleTags(articleId);
    }
    
    private String getObjectNameFromUrl(String url) {
        if (url == null) {
            Asserts.fail("文件URL不能为空");
        }
        return url.substring(url.indexOf(StorageConstants.ARTICLE_CONTENT_DIR));
    }
    
    /**
     * 更新文章标签关联
     * @param articleId 文章ID
     * @param tagIds 标签ID列表
     */
    private void updateArticleTags(Long articleId, List<Long> tagIds) {
        // 1. 删除原有的标签关联
        articleRepository.deleteArticleTags(articleId);
        
        // 2. 如果有新的标签，添加新的关联
        if (tagIds != null && !tagIds.isEmpty()) {
            articleRepository.insertArticleTags(articleId, tagIds);
        }
    }
} 