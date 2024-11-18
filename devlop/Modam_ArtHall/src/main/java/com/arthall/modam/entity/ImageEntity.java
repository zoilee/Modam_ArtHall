package com.arthall.modam.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "images")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reference_id", nullable = false)
    private int referenceId;

    @Column(name = "reference_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;

    @Column(name = "alt_text", length = 100)
    private String altText;

    @Column(name = "uploaded_at")
    private Timestamp uploadedAt;

    public enum ReferenceType {
        PERFORMANCE, NOTICE, REVIEW
    }

}
