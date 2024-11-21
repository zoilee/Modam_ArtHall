package com.arthall.modam.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.ReservationEntity;
import com.arthall.modam.repository.ReservationRepository;


@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public List<ReservationEntity> getUpcomingReservationsByUserId(int userId) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return reservationRepository.findByUserEntity_IdAndReservationDateAfter(userId, now);
    }

    public List<ReservationEntity> getPastReservationsByUserId(int userId) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return reservationRepository.findByUserEntity_IdAndReservationDateBefore(userId, now);
    }

    public ReservationEntity createReservation(ReservationEntity reservationEntity) {
        // 예약을 DB에 저장
        reservationEntity.setReservationDate(new Timestamp(System.currentTimeMillis())); // 예약 날짜 설정
        reservationEntity.setStatus("CONFIRMED"); // 기본 상태 설정

        return reservationRepository.save(reservationEntity); // 저장 후 반환
    }

}
