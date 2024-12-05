package com.arthall.modam.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationsDataDto {
    private String performanceTitle;
    private Date reservationDate; // java.sql.Date 또는 java.util.Date
    private Long totalReservations;
}
