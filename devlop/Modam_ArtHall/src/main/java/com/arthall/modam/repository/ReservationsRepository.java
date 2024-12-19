package com.arthall.modam.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.arthall.modam.entity.ReservationsEntity;

public interface ReservationsRepository extends JpaRepository<ReservationsEntity, Integer> {

        @Query(value = "SELECT * FROM reservations r WHERE r.user_id = :userId AND r.reservation_date > :reservationDate", nativeQuery = true)
        List<ReservationsEntity> findByUserIdAndReservationDateAfter(@Param("userId") int userId,
                        @Param("reservationDate") Timestamp date);

        @Query(value = "SELECT * FROM reservations r WHERE r.user_id = :userId AND r.reservation_date < :reservationDate", nativeQuery = true)
        List<ReservationsEntity> findByUserIdAndReservationDateBefore(@Param("userId") int userId,
                        @Param("reservationDate") Timestamp date);

        // 미래 예약 조회
        @Query("SELECT r FROM ReservationsEntity r JOIN r.showEntity s WHERE r.userEntity.id = :userId AND s.showDate >= :today ORDER BY s.showDate ASC")
        List<ReservationsEntity> findUpcomingReservationsByShowDate(@Param("userId") int userId,
                        @Param("today") LocalDate today);

        // 과거 예약 조회
        @Query("SELECT r FROM ReservationsEntity r JOIN r.showEntity s WHERE r.userEntity.id = :userId AND s.showDate < :today ORDER BY s.showDate DESC")
        List<ReservationsEntity> findPastReservationsByShowDate(@Param("userId") int userId,
                        @Param("today") LocalDate today);

        @Query("SELECT r.ticket FROM ReservationsEntity r WHERE r.id = :id")
        String findTicketById(@Param("id") int id);

        // 공연 ID로 예약 수 카운트하는 메서드 추가
        @Query(value = "SELECT COUNT(*) FROM reservations r " +
                        "JOIN shows s ON r.show_id = s.id " +
                        "WHERE s.performance_id = :performanceId AND r.status = 'CONFIRMED'", nativeQuery = true)
        int countByPerformanceId(@Param("performanceId") int performanceId);

        List<ReservationsEntity> findByUserEntity_IdAndReservationDateBefore(int userId, Timestamp date);

        List<ReservationsEntity> findByShowEntity_Id(int showId);

        List<ReservationsEntity> findByUserId(int userId);

        @Query("SELECT s.showDate AS showDate, p.title AS performanceTitle, COUNT(r.id) AS totalReservations " +
                        "FROM ReservationsEntity r " +
                        "JOIN r.showEntity s " +
                        "JOIN s.performancesEntity p " +
                        "WHERE r.status <> 'CANCEL' AND s.showDate BETWEEN CURRENT_DATE AND :endDate " +
                        "GROUP BY s.showDate, p.title " +
                        "ORDER BY s.showDate, p.title")
        List<Object[]> findReservationsByShowDate(@Param("endDate") Date endDate);

        List<ReservationsEntity> findByShowEntity_IdAndStatus(int showId, String status);

        @Query("SELECT r FROM ReservationsEntity r " +
                        "JOIN FETCH r.showEntity s " +
                        "JOIN FETCH s.performancesEntity p " +
                        "WHERE DATE(r.reservationDate) = CURRENT_DATE AND r.status = 'CONFIRMED'")
        List<ReservationsEntity> findTodayConfirmedReservations();
}
