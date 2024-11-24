package com.arthall.modam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Data
@Table(name = "notices")
public class NoticesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "admin_id", nullable = false)
    private int adminId; // 관리자 ID

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "reference_id", referencedColumnName = "id", foreignKey = @jakarta.persistence.ForeignKey(value = jakarta.persistence.ConstraintMode.NO_CONSTRAINT))
    @SQLRestriction("reference_type = 'NOTICE'")
    private List<ImagesEntity> imagesEntities;

}
