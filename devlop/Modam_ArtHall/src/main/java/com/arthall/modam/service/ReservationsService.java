package com.arthall.modam.service;

import java.sql.Timestamp;
import java.time.LocalDate;
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

}
