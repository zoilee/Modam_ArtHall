package com.arthall.modam.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.arthall.modam.dto.MusicalDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class ShowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int showId;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private MusicalDto musical;  // 어떤 뮤지컬의 회차인지를 나타냄

    private LocalDate showDate; // 공연 날짜
    private int showTime;  // 회차 시간 (1회차, 2회차)
    private int seatLimit;

    // getters and setters
}
