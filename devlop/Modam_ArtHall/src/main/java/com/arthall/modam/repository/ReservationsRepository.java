package com.arthall.modam.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.ReservationsEntity;

public interface ReservationsRepository extends JpaRepository<ReservationsEntity, Integer> {
    // 미래 예약 조회
    List<ReservationsEntity> findByUserEntityIdAndReservationDateAfter(int userId, Timestamp reservationDate);

    // 과거 예약 조회
    List<ReservationsEntity> findByUserEntityIdAndReservationDateBefore(int userId, Timestamp reservationDate);
}
