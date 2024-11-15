package com.arthall.modam.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ReservationDto {

    private int id;
    private int userId;
    private int performanceId;
    private int seatId;
    private Timestamp reservationDate;
    private String status;
}
