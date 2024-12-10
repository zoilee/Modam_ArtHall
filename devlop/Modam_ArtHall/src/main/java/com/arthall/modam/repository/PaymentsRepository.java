package com.arthall.modam.repository;

import java.util.List;

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

    
    @Query("SELECT p.title, SUM(pmt.reAmount) " +
            "FROM PaymentsEntity pmt " +
            "JOIN pmt.reservation r " +
            "JOIN r.showEntity s " +
            "JOIN s.performancesEntity p " +
            "WHERE p.endDate >= CURRENT_DATE " +
            "AND pmt.transactionType = 'PAYMENT' " +
            "GROUP BY p.title " +
            "HAVING SUM(pmt.reAmount) > 0")
    List<Object[]> findPerformancesWithTotalSales();

    // 오늘의 결제 금액
    @Query("SELECT SUM(p.amount) FROM PaymentsEntity p WHERE p.transactionType = 'PAYMENT' AND DATE(p.createdAt) = CURRENT_DATE")
    Double findTodayPayments();

    // 오늘의 환불 금액
    @Query("SELECT SUM(p.amount) FROM PaymentsEntity p WHERE p.transactionType = 'REFUND' AND DATE(p.createdAt) = CURRENT_DATE")
    Double findTodayRefunds();

    // 오늘의 적립금 사용 금액
    @Query("SELECT SUM(p.reAmount) FROM PaymentsEntity p WHERE p.transactionType = 'PAYMENT' AND DATE(p.createdAt) = CURRENT_DATE")
    Double findTodayCreditsUsed();

    // 총 결제 금액
    @Query("SELECT SUM(p.amount) FROM PaymentsEntity p WHERE p.transactionType = 'PAYMENT'")
    Double findTotalPayments();

    // 총 환불 금액
    @Query("SELECT SUM(p.amount) FROM PaymentsEntity p WHERE p.transactionType = 'REFUND'")
    Double findTotalRefunds();

    // 총 적립금 사용 금액
    @Query("SELECT SUM(p.reAmount) FROM PaymentsEntity p WHERE p.transactionType = 'PAYMENT'")
    Double findTotalCreditsUsed();
}
