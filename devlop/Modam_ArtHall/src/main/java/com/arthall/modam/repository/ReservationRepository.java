package com.arthall.modam.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.ReservationEntity;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {
    List<ReservationEntity> findByUserEntity_IdAndReservationDateAfter(int userId, Timestamp date);
    List<ReservationEntity> findByUserEntity_IdAndReservationDateBefore(int userId, Timestamp date);
}
