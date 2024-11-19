package com.arthall.modam.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;

import com.arthall.modam.entity.CommentEntity;
import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public List<CommentEntity> getComments(PerformancesEntity performance, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit); // 페이지 계산
        return commentRepository.findByPerformanceOrderByCreatedAtDesc(performance, pageRequest).getContent();
    }

    public CommentEntity addComment(UserEntity user, PerformancesEntity performance, String commentText,
            Integer rating) {
        CommentEntity comment = new CommentEntity();
        comment.setUser(user);
        comment.setPerformance(performance);
        comment.setCommentText(commentText);
        comment.setRating(rating);
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return commentRepository.save(comment);
    }

    public CommentEntity updateComment(int commentId, String commentText) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setCommentText(commentText);
        return commentRepository.save(comment);
    }

    public void deleteComment(int commentId) {
        commentRepository.deleteById(commentId);
    }

    // 공연 ID에 대한 댓글 총 개수 반환
    public int getTotalCommentsByPerformanceId(int performanceId) {
        return (int) commentRepository.countByPerformanceId(performanceId);
    }
}