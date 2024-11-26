package com.arthall.modam.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "reservations")
public class ReservationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "show_id", referencedColumnName = "show_id")
    private ShowEntity showEntity;

    @Column(name = "seat_id1", length = 10)
    private String seatId1;

    @Column(name = "seat_id2", length = 10)
    private String seatId2;

    @CreationTimestamp
    @Column(name = "reservation_date", nullable = false, updatable = false)
    private Timestamp reservationDate;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "ticket", length = 100)
    private String ticket;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private PaymentsEntity payment;

    @OneToMany(mappedBy = "reservationsEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RewardsLogEntity> rewardsLogEntities; // RewardsLogEntity와 연결
}