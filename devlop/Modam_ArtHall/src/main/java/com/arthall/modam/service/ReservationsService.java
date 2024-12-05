package com.arthall.modam.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.ReservationsEntity;
import com.arthall.modam.repository.ReservationsRepository;

@Service
public class ReservationsService {
    @Autowired
    private ReservationsRepository reservationRepository;

    public List<ReservationsEntity> getUpcomingReservations(int userId) {
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();
        // JPA 쿼리 호출
        return reservationRepository.findUpcomingReservationsByShowDate(userId, today);
    }

    public List<ReservationsEntity> getPastReservations(int userId) {
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();
        // JPA 쿼리 호출
        return reservationRepository.findPastReservationsByShowDate(userId, today);
    }

    public ReservationsEntity createReservation(ReservationsEntity reservationEntity) {
        // 예약을 DB에 저장
        reservationEntity.setReservationDate(new Timestamp(System.currentTimeMillis())); // 예약 날짜 설정
        reservationEntity.setStatus("CONFIRMED"); // 기본 상태 설정

        return reservationRepository.save(reservationEntity); // 저장 후 반환
    }

    // 예약이 완료된 좌석 가져오기
    public List<String> getUnavailableSeats(int showId) {
        List<ReservationsEntity> reservations = reservationRepository.findByShowEntity_IdAndStatus(showId, "confirmed");

        List<String> unavailableSeats = new ArrayList<>();
        for (ReservationsEntity reservation : reservations) {
            if (reservation.getSeatId1() != null) {
                unavailableSeats.add(reservation.getSeatId1());
            }
            if (reservation.getSeatId2() != null) {
                unavailableSeats.add(reservation.getSeatId2());
            }
        }
        return unavailableSeats;
    }

    // 오늘 결제된 예약 목록 가져오기
    public List<ReservationsEntity> getTodayPaidReservations() {
        List<ReservationsEntity> reservations = reservationRepository.findTodayPaidReservations();
        if (reservations == null || reservations.isEmpty()) {
            System.out.println("No reservations found for today.");
        } else {
            System.out.println("Reservations found: " + reservations.size());
        }
        return reservations;
    }
}
