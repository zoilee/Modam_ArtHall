package com.arthall.modam.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "images")
@Data
public class ImagesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 기본 키

    @Column(name = "reference_id", nullable = false)
    private int referenceId; // 참조 ID (다른 테이블과 연관)

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", columnDefinition = "enum('performance','notice','review')", nullable = false)
    private ReferenceType referenceType; // 참조 타입 ('performance', 'notice', 'review')

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl; // 이미지 URL

    @Column(name = "alt_text", length = 100)
    private String altText; // 이미지 대체 텍스트

    @Column(name = "uploaded_at", nullable = false)
    private Timestamp uploadedAt; // 업로드 시간

    public enum ReferenceType {
        PERFORMANCE, NOTICE, REVIEW
    }
}