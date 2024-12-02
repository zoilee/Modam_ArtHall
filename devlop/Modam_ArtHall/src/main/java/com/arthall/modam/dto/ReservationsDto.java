package com.arthall.modam.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class ReservationsDto {

    private int id;
    private int userId;
    private int showId;
    private String seatId1;
    private String seatId2;

    private Timestamp reservationDate;
    private BigDecimal totalPrice;
    private String status;
    private String ticket;

}
