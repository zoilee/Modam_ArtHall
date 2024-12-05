package com.arthall.modam.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
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
        @Query("SELECT r FROM ReservationsEntity r WHERE r.userEntity.id = :userId AND r.showEntity.showDate >= :today")
        List<ReservationsEntity> findUpcomingReservationsByShowDate(@Param("userId") int userId,
                        @Param("today") LocalDate today);

        // 과거 예약 조회
        @Query("SELECT r FROM ReservationsEntity r WHERE r.userEntity.id = :userId AND r.showEntity.showDate < :today")
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

        List<ReservationsEntity> findByShowEntity_IdAndStatus(int showId, String status);


        // 조인 쿼리로 오늘의 예약 및 결제된 예약 가져오기
        @Query(value = "SELECT * FROM reservations r " +
               "JOIN payments p ON r.id = p.reservation_id " +
               "WHERE p.status = 'paid' " +
               "AND DATE(r.reservation_date) = CURDATE()", nativeQuery = true)
        List<ReservationsEntity> findTodayPaidReservations();
}
