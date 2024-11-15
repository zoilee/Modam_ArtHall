package com.arthall.modam.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER) // 댓글 작성자를 위한 UserEntity와의 관계 설정
    @JoinColumn(name = "user_id", nullable = false) // user_id 외래 키와 매핑
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER) // 공연 정보를 위한 PerformanceEntity와의 관계 설정
    @JoinColumn(name = "performance_id", nullable = false) // performance_id 외래 키와 매핑
    private PerformanceEntity performance;

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "rating", nullable = false)
    private Integer rating;  // 평점 필드 추가
}
