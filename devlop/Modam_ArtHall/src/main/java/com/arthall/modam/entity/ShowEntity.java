package com.arthall.modam.entity;

import java.sql.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "show_entity")
public class ShowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int showId;

    @ManyToOne(fetch = FetchType.LAZY) // ManyToOne 관계 설정
    @JoinColumn(name = "performance_id", referencedColumnName = "performanceId", nullable = false) // 외래키 설정
    private PerformanceEntity performanceEntity;

    private Date showDate;
    private int showTime; //회차
    private int seatLimit; //가로18 * 세로12 216
    private int seatAvailable; //잔여석

    // 기본 생성자
    public ShowEntity() {}

    // 생성자 (필드 초기화용)
    public ShowEntity(int performanceId, int showId, Date showDate, int showTime, int seatLimit, int seatAvailable) {
        this.performanceEntity = performanceEntity;
        this.showId = showId;
        this.showDate = showDate;
        this.showTime = showTime;
        this.seatLimit = seatLimit;
        this.seatAvailable = seatAvailable;
    }

    // Getter and Setter methods
    public int getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(int performanceId) {
        this.performanceId = performanceId;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    public int getSeatLimit() {
        return seatLimit;
    }

    public void setSeatLimit(int seatLimit) {
        this.seatLimit = seatLimit;
    }

    public int getSeatAvailable() {
        return seatAvailable;
    }

    public void setSeatAvailable(int seatAvailable) {
        this.seatAvailable = seatAvailable;
    }
}

