package com.arthall.modam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.PaymentsEntity;
import com.arthall.modam.entity.ReservationsEntity;

@Repository
public interface PaymentsRepository extends JpaRepository<PaymentsEntity, Integer> {
    PaymentsEntity findByReservation(ReservationsEntity reservation);

    // 오늘의 매출 가져오기
    @Query("SELECT SUM(p.amount) FROM PaymentsEntity p WHERE p.transactionType = 'PAYMENT' AND DATE(p.createdAt) = CURRENT_DATE")
    Double findTodayTotalSales();
    // 총 매출
    @Query("SELECT SUM(p.amount) FROM PaymentsEntity p WHERE p.transactionType = 'PAYMENT'")
    Double findTotalSales();
}
