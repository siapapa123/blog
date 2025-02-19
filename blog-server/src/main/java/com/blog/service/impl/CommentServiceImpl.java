package com.blog.service.impl;

import com.blog.common.response.ResultCode;
import com.blog.common.util.SecurityUtils;
import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.dto.response.UserResponse;
import com.blog.exception.Asserts;
import com.blog.model.Article;
import com.blog.model.Comment;
import com.blog.model.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.UserRepository;
import com.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponse create(CommentRequest request) {
        // 检查文章是否存在
        Article article = articleRepository.selectById(request.getArticleId());
        if (article == null || article.getIsDeleted()) {
            Asserts.fail(ResultCode.ARTICLE_NOT_FOUND);
        }

        // 检查父评论是否存在
        if (request.getParentId() != null) {
            Comment parentComment = commentRepository.selectById(request.getParentId());
            if (parentComment == null) {
                Asserts.fail(ResultCode.COMMENT_NOT_FOUND);
            }
        }

        // 创建评论
        Comment comment = Comment.builder()
                .content(request.getContent())
                .articleId(request.getArticleId())
                .userId(SecurityUtils.getCurrentUserId())
                .parentId(request.getParentId())
                .createTime(LocalDateTime.now())
                .build();

        commentRepository.insert(comment);
        return convertToVO(comment, null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Comment comment = commentRepository.selectById(id);
        if (comment == null) {
            Asserts.fail(ResultCode.COMMENT_NOT_FOUND);
        }

        // 检查权限
        if (!isCommentAuthor(id)) {
            Asserts.fail(ResultCode.NO_PERMISSION);
        }

        // 删除评论及其子评论
        commentRepository.deleteById(id);
        List<Comment> children = commentRepository.selectByParentId(id);
        for (Comment child : children) {
            commentRepository.deleteById(child.getId());
        }
    }

    @Override
    public List<CommentResponse> getArticleComments(Long articleId) {
        // 获取文章的所有评论
        List<Comment> comments = commentRepository.selectByArticleId(articleId);
        
        // 获取所有评论用户信息
        List<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, User> userMap = userRepository.selectByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 构建评论树
        Map<Long, List<Comment>> parentIdMap = comments.stream()
                .collect(Collectors.groupingBy(comment -> 
                    comment.getParentId() == null ? 0L : comment.getParentId()));

        // 转换顶级评论
        return parentIdMap.getOrDefault(0L, new ArrayList<>()).stream()
                .map(comment -> convertToVO(comment, 
                        buildChildren(comment.getId(), parentIdMap, userMap), 
                        userMap.get(comment.getUserId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getUserComments(Long userId) {
        List<Comment> comments = commentRepository.selectByUserId(userId);
        User user = userRepository.selectById(userId);
        return comments.stream()
                .map(comment -> convertToVO(comment, null, user))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCommentAuthor(Long commentId) {
        Comment comment = commentRepository.selectById(commentId);
        if (comment == null) {
            return false;
        }
        return SecurityUtils.getCurrentUserId().equals(comment.getUserId());
    }

    private List<CommentResponse> buildChildren(Long parentId, 
            Map<Long, List<Comment>> parentIdMap, Map<Long, User> userMap) {
        return parentIdMap.getOrDefault(parentId, new ArrayList<>()).stream()
                .map(comment -> convertToVO(comment, 
                        buildChildren(comment.getId(), parentIdMap, userMap),
                        userMap.get(comment.getUserId())))
                .collect(Collectors.toList());
    }

    private CommentResponse convertToVO(Comment comment, List<CommentResponse> children) {
        User user = userRepository.selectById(comment.getUserId());
        return convertToVO(comment, children, user);
    }

    private CommentResponse convertToVO(Comment comment, List<CommentResponse> children, User user) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(convertUserToVO(user))
                .createTime(comment.getCreateTime())
                .children(children)
                .build();
    }

    private UserResponse convertUserToVO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
} 