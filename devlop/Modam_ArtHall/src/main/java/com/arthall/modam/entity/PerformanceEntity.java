package com.arthall.modam.entity;

import java.sql.Date;
import java.sql.Timestamp;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class PerformanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private int time;  // 공연 시간 (예: 2시간, 3시간 등)
    private int age;   // 연령 제한
    private String location;

    @Column(name = "created_at")
    private Timestamp createdAt;

}
