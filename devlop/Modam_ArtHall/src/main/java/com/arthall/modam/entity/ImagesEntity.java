package com.arthall.modam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Table(name = "images")
public class ImagesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reference_id", nullable = false)
    private Integer referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = false)
    private ReferenceType referenceType;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "alt_text", length = 100)
    private String altText;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false)
    private Timestamp uploadedAt;

    public enum ReferenceType {
        PERFORMANCE, NOTICE, REVIEW
    }
}
