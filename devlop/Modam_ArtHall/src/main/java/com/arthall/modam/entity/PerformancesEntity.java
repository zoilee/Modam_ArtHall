package com.arthall.modam.entity;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "performances")
@Data
public class PerformancesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private Date start_date;

    @Column
    private Date end_date;

    @Column
    private int time;

    @Column(length = 100)
    private String location;

    @Column
    private int age;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt; // 생성 시간 자동 설정

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "reference_id", referencedColumnName = "id")
    @SQLRestriction("reference_type = 'PERFORMANCE'")
    private List<ImagesEntity> imagesEntities;

}
