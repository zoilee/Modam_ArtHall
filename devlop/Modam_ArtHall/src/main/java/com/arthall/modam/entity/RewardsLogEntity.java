package com.arthall.modam.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "rewards_log")
public class RewardsLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "reservations_id", nullable = false)
    private Integer reservationsId; // reservations 테이블 id

    @Column(name = "total_point", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPoint; // 총 적립금

    @Column(name = "change_point", precision = 10, scale = 2, nullable = false)
    private BigDecimal changePoint; // 변동되는 적립금

    @Column(name = "description", length = 255)
    private String description; // 설명 EARN USE REFUND

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    
}
