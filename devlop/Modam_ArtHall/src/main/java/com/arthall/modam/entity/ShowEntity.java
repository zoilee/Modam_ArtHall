package com.arthall.modam.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "shows")
@Data
public class ShowEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "show_date")
    private Date showDate;

    @Column(name = "show_time")
    private int showTime; // 회차 (1은 13시, 2는 17시 회차)

    @Column(name = "seat_limit")
    private int seatLimit;

    @Column(name = "seat_available")
    private int seatAvailable;

    // 외래 키
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "performance_id", referencedColumnName = "id")
    private PerformancesEntity performancesEntity;

}
