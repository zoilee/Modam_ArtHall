package com.arthall.modam.entity;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "performances")
public class PerformanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // 공연 시작 날짜

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate; // 공연 종료 날짜

    @Column(nullable = false)
    private LocalTime time; // 공연 시간

    @Column(nullable = false, length = 100)
    private String location;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis()); // 생성 시 자동 설정

    @Column
    private Integer age; // 관람 연령 제한

    @Transient
    private String formattedAverageRating; // 평균 평점 형식

}