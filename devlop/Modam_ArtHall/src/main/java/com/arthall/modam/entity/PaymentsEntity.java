package com.arthall.modam.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "payments")
public class PaymentsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationsEntity reservation; // 예약 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType; // 'PAYMENT' 또는 'REFUND' 'PART_REFUND'

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // 금액

    @Column(name = "method", nullable = false, length = 50)
    private String method; // 결제 수단

    @Column(name = "status", nullable = false, length = 20)
    private String status; // 상태 (예: 'pending', 'completed')

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Timestamp createdAt;

    @Column(name = "reAmount", nullable = false, precision = 10, scale = 2)
    private BigDecimal reAmount;

    public enum TransactionType {
        PAYMENT, REFUND, PART_REFUND
    }
}
