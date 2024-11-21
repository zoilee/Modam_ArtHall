package com.arthall.modam.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.ReservationsEntity;
import com.arthall.modam.repository.ReservationsRepository;

@Service
public class ReservationService {
    @Autowired
    private ReservationsRepository reservationRepository;

    public List<ReservationsEntity> getUpcomingReservationsByUserId(int userId) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return reservationRepository.findByUserIdAndReservationDateAfter(userId, now);
    }

    public List<ReservationsEntity> getPastReservationsByUserId(int userId) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return reservationRepository.findByUserIdAndReservationDateBefore(userId, now);
    }

}
