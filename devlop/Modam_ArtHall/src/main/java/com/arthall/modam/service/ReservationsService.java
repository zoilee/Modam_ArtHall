package com.arthall.modam.service;

import java.sql.Timestamp;
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
        // 현재 날짜를 기준으로 미래의 예약을 가져옵니다.
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return reservationRepository.findByUserEntityIdAndReservationDateAfter(userId, now);
    }

    public List<ReservationsEntity> getPastReservations(int userId) {
        // 현재 날짜를 기준으로 과거의 예약을 가져옵니다.
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return reservationRepository.findByUserEntityIdAndReservationDateBefore(userId, now);
    }

    public ReservationsEntity createReservation(ReservationsEntity reservationEntity) {
        // 예약을 DB에 저장
        reservationEntity.setReservationDate(new Timestamp(System.currentTimeMillis())); // 예약 날짜 설정
        reservationEntity.setStatus("CONFIRMED"); // 기본 상태 설정

        return reservationRepository.save(reservationEntity); // 저장 후 반환
    }

}
