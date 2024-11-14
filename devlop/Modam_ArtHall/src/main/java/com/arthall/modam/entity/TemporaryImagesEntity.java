package com.arthall.modam.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "temporary_images")
@Data
public class TemporaryImagesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;

    @Column(name = "referenceToken", length = 255, nullable = false)
    private String referenceToken;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false)
    private Timestamp uploadedAt;

}
