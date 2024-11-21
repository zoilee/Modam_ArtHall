package com.arthall.modam.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "reservations")
public class ReservationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 기본 키

    @Column(name = "user_id", nullable = false)
    private int userId; // 사용자 ID

    @Column(name = "show_id") //
    private Integer showId; // 공연 정보

    @Column(name = "seat_id1", length = 10) // 첫 번째 좌석 ID
    private String seatId1;

    @Column(name = "seat_id2", length = 10) // 두 번째 좌석 ID
    private String seatId2;

    @Column(name = "reservation_date", nullable = false)
    private Timestamp reservationDate; // 예약 날짜

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice; // 총 금액

    @Column(name = "status", length = 20)
    private String status; // 예약 상태

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private PaymentsEntity payment;
}
