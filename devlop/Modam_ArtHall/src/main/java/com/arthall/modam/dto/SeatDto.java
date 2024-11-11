package com.arthall.modam.dto;

import lombok.Data;

@Data
public class SeatDto {
    private int seatId;
    private String seatNum;
    private int seatClass;
    private int seatPrice;
    private boolean isReserved;
}
