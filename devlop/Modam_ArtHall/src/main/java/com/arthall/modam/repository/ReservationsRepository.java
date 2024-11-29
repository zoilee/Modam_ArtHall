package com.arthall.modam.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.arthall.modam.entity.ReservationsEntity;

public interface ReservationsRepository extends JpaRepository<ReservationsEntity, Integer> {
    // 미래 예약 조회
    @Query("SELECT r FROM ReservationsEntity r WHERE r.userEntity.id = :userId AND r.showEntity.showDate >= :today")
    List<ReservationsEntity> findUpcomingReservationsByShowDate(@Param("userId") int userId,
            @Param("today") LocalDate today);

    // 과거 예약 조회
    @Query("SELECT r FROM ReservationsEntity r WHERE r.userEntity.id = :userId AND r.showEntity.showDate < :today")
    List<ReservationsEntity> findPastReservationsByShowDate(@Param("userId") int userId,
            @Param("today") LocalDate today);

    @Query("SELECT r.ticket FROM ReservationsEntity r WHERE r.id = :id")
    String findTicketById(@Param("id") int id);
}
