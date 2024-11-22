package com.arthall.modam.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthall.modam.entity.ReservationsEntity;

public interface ReservationsRepository extends JpaRepository<ReservationsEntity, Integer> {
    List<ReservationsEntity> findByUserEntity_IdAndReservationDateAfter(int userId, Timestamp date);

    List<ReservationsEntity> findByUserEntity_IdAndReservationDateBefore(int userId, Timestamp date);

    List<ReservationsEntity> findByShowEntity_Id(int showId);
}
