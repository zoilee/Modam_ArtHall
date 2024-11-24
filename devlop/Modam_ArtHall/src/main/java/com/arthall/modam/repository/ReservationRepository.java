package com.arthall.modam.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.arthall.modam.entity.ReservationEntity;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {
    List<ReservationEntity> findByUserIdAndReservationDateAfter(int userId, Timestamp date);
    List<ReservationEntity> findByUserIdAndReservationDateBefore(int userId, Timestamp date);

    // 공연 ID로 예약 수 카운트하는 메서드 추가
    int countByPerformanceId(int performanceId);
}
