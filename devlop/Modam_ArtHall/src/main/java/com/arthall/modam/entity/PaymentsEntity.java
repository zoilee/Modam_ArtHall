package com.arthall.modam.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class PaymentsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation; // 예약 ID
    
    @Enumerated(EnumType.STRING) 
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType; // 'payment' 또는 'refund'

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // 금액

    @Column(name = "method",nullable = false, length = 50)
    private String method; // 결제 수단 

    @Column(name = "status",nullable = false, length = 20)
    private String status; // 상태 (예: 'pending', 'completed')

    @Column(name = "paymentUid",nullable = false, length = 20)
    private String PaymentUid; //결제 고유번호

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    public enum TransactionType {
        PAYMENT, REFUND
    }
}
