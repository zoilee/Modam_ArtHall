package com.arthall.modam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthall.modam.dto.ReservationDto;
import com.arthall.modam.dto.SeatDto;
import com.arthall.modam.repository.ReservationRepository;
import com.arthall.modam.repository.SeatRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private SeatRepository seatRepository;

    public void createReservation(ReservationDto reservation) {
        reservationRepository.save(reservation);
        for (SeatDto seat : reservation.getReservedSeats()) {
            seat.setBooked(true);
            seatRepository.save(seat);
        }
    }
}

