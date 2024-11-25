package com.arthall.modam.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.ReservationEntity;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {
    List<ReservationEntity> findByUserIdAndReservationDateAfter(int userId, Timestamp date);
    List<ReservationEntity> findByUserIdAndReservationDateBefore(int userId, Timestamp date);
}
