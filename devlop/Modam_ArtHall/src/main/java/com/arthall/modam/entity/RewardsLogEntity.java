package com.arthall.modam.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private Integer reservationsId;

    @Column(name = "total_point", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPoint;

    @Column(name = "change_point", precision = 10, scale = 2, nullable = false)
    private BigDecimal changePoint;

    @Column(name = "description", length = 255)
    private String description;

    @CreationTimestamp

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "reservations_id", insertable = false, updatable = false)
    @JsonIgnore // 순환 참조 방지
    private ReservationsEntity reservationsEntity;
}