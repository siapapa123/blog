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
import com.blog.repository.ArticleRepository;
import com.blog.repository.UserRepository;
import com.blog.repository.CategoryRepository;
import com.blog.model.User;
import com.blog.model.Category;
import com.blog.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    // ========== 核心CRUD方法 ==========
    @Override
    @Transactional
    public Article create(ArticleCreateRequest request) {
        // 1. 保存文章内容到MinIO
        String contentUrl = fileService.uploadContent(
            request.getContent(),
            StorageConstants.ARTICLE_CONTENT_DIR,
            UUID.randomUUID().toString() + ".md"
        );
        
        // 2. 如果有封面图片，上传到MinIO
        String coverUrl = null;
        if (request.getCover() != null && !request.getCover().isEmpty()) {
            coverUrl = fileService.uploadFile(
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
        String content = fileService.getContent(getObjectNameFromUrl(article.getContentUrl()));
        
        // 3. 获取分类信息
        String categoryName = null;
        if (article.getCategoryId() != null) {
            Category category = categoryRepository.selectById(article.getCategoryId());
            categoryName = category != null ? category.getName() : null;
        }
        
        return ArticleDetailResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(content)
                .summary(article.getSummary())
                .coverUrl(article.getCoverUrl())
                .authorId(article.getAuthorId())
                .authorName(getUserName(article.getAuthorId()))
                .categoryId(article.getCategoryId())
                .categoryName(categoryName)
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
            String contentUrl = fileService.uploadContent(
                request.getContent(),
                StorageConstants.ARTICLE_CONTENT_DIR,
                UUID.randomUUID().toString() + ".md"
            );
            article.setContentUrl(contentUrl);
        }
        
        // 3. 如果有新的封面图片，更新到MinIO
        if (request.getCover() != null && !request.getCover().isEmpty()) {
            String coverUrl = fileService.uploadFile(
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

    // ========== 列表查询方法 ==========
    @Override
    public PageInfo<ArticleDetailResponse> getList(int pageNum, int pageSize, Long categoryId, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleRepository.selectList(categoryId, keyword);
        
        // 批量获取所有需要的分类ID
        List<Long> categoryIds = articles.stream()
                .map(Article::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
                
        // 一次性查询所有需要的分类
        Map<Long, String> categoryMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            categoryMap = categoryRepository.selectByIds(categoryIds).stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
        }

        // 使用缓存的分类信息转换文章
        Map<Long, String> finalCategoryMap = categoryMap;
        return new PageInfo<>(articles.stream()
                .map(article -> convertToDetailResponse(article, finalCategoryMap))
                .collect(Collectors.toList()));
    }

    @Override
    public PageInfo<ArticleDetailResponse> getList(int pageNum, int pageSize, Long categoryId, 
            Long tagId, String keyword, String sortField, String sortDirection) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleRepository.selectListWithParams(
                categoryId, 
                tagId, 
                keyword, 
                sortField, 
                "desc".equalsIgnoreCase(sortDirection)
        );
        
        // 批量获取分类信息
        Map<Long, String> categoryMap = getCategoryMapForArticles(articles);
        
        return new PageInfo<>(articles.stream()
                .map(article -> convertToDetailResponse(article, categoryMap))
                .collect(Collectors.toList()));
    }

    @Override
    public List<ArticleDetailResponse> getHotArticles(int limit) {
        // 获取浏览量最高的文章
        List<Article> hotArticles = articleRepository.selectHotArticles(limit);
        
        // 批量获取分类信息
        Map<Long, String> categoryMap = getCategoryMapForArticles(hotArticles);
        
        return hotArticles.stream()
                .map(article -> convertToDetailResponse(article, categoryMap))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDetailResponse> getRecommendedArticles(Long articleId, int limit) {
        // 1. 获取当前文章的分类和标签
        Article currentArticle = articleRepository.selectById(articleId);
        if (currentArticle == null) {
            Asserts.fail("文章不存在");
        }
        
        // 2. 获取相同分类或标签的文章
        List<Article> recommendedArticles = articleRepository.selectRecommendedArticles(
                articleId,
                currentArticle.getCategoryId(),
                getArticleTags(articleId).stream()
                        .map(TagResponse::getId)
                        .collect(Collectors.toList()),
                limit
        );
        
        // 3. 批量获取分类信息
        Map<Long, String> categoryMap = getCategoryMapForArticles(recommendedArticles);
        
        return recommendedArticles.stream()
                .map(article -> convertToDetailResponse(article, categoryMap))
                .collect(Collectors.toList());
    }

    @Override
    public PageInfo<ArticleDetailResponse> getUserArticles(Long userId, int pageNum, int pageSize) {
        // 验证用户是否存在
        User user = userRepository.selectById(userId);
        if (user == null) {
            Asserts.fail("用户不存在");
        }
        
        // 分页查询用户的文章
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleRepository.selectByAuthorId(userId);
        
        // 批量获取分类信息
        Map<Long, String> categoryMap = getCategoryMapForArticles(articles);
        
        return new PageInfo<>(articles.stream()
                .map(article -> convertToDetailResponse(article, categoryMap))
                .collect(Collectors.toList()));
    }

    // ========== 状态操作方法 ==========
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

    @Override
    public boolean isArticleAuthor(Long articleId) {
        Article article = articleRepository.selectById(articleId);
        if (article == null) {
            return false;
        }
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return currentUserId != null && currentUserId.equals(article.getAuthorId());
    }

    // ========== 私有辅助方法 ==========
    private ArticleDetailResponse convertToDetailResponse(Article article, Map<Long, String> categoryMap) {
        return ArticleDetailResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .summary(article.getSummary())
                .coverUrl(article.getCoverUrl())
                .authorId(article.getAuthorId())
                .authorName(getUserName(article.getAuthorId()))
                .categoryId(article.getCategoryId())
                .categoryName(getCategoryName(article.getCategoryId(), categoryMap))
                .status(article.getStatus())
                .viewCount(article.getViewCount())
                .isTop(article.getIsTop())
                .createTime(article.getCreateTime())
                .updateTime(article.getUpdateTime())
                .tags(getArticleTags(article.getId()))
                .build();
    }
    
    private String getCategoryName(Long categoryId, Map<Long, String> categoryMap) {
        return categoryId != null ? categoryMap.get(categoryId) : null;
    }
    
    private String getUserName(Long userId) {
        User user = userRepository.selectById(userId);
        return user != null ? user.getUsername() : null;
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
        articleRepository.deleteArticleTags(articleId);
        if (tagIds != null && !tagIds.isEmpty()) {
            articleRepository.insertArticleTags(articleId, tagIds);
        }
    }

    private Map<Long, String> getCategoryMapForArticles(List<Article> articles) {
        List<Long> categoryIds = articles.stream()
                .map(Article::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
                
        if (categoryIds.isEmpty()) {
            return new HashMap<>();
        }
        
        return categoryRepository.selectByIds(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }
} 