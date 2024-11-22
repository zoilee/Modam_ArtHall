package com.arthall.modam.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.hibernate.annotations.SQLRestriction;

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

@Data
@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "show_id", referencedColumnName = "show_id") // show_id 컬럼을 참조
    private ShowEntity showEntity; // 필드 확인

    @Column(name = "seat_id1")
    private String seatId1;

    @Column(name = "seat_id2")
    private String seatId2;

    @Column(name = "reservation_date", nullable = false)
    private Timestamp reservationDate;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "status", length = 20)
    private String status;
}
