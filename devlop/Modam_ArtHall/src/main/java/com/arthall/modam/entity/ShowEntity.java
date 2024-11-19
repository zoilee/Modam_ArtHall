package com.arthall.modam.entity;


import java.sql.Date;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ShowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int showId;

    private Date showDate;
    
    @Column(name = "show_time")
    private int showTime; // 회차 (1은 13시, 2는 17시 회차)
    
    @Column(name = "seat_limit")
    private int seatLimit;
    
    @Column(name = "seat_available")
    private int seatAvailable;

    // 외래 키
    @ManyToOne
    @JoinColumn(name = "performance_id")
    private PerformanceEntity performance;

}
