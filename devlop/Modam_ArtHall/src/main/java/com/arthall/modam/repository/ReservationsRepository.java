package com.arthall.modam.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.ReservationsEntity;

public interface ReservationsRepository extends JpaRepository<ReservationsEntity, Integer> {
    List<ReservationsEntity> findByUserIdAndReservationDateAfter(int userId, Timestamp date);

    List<ReservationsEntity> findByUserIdAndReservationDateBefore(int userId, Timestamp date);
}
