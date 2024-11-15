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
        return reservationRepository.findByUserIdAndReservationDateAfter(userId, now);
    }

    public List<ReservationEntity> getPastReservationsByUserId(int userId) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return reservationRepository.findByUserIdAndReservationDateBefore(userId, now);
    }


}
