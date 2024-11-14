package com.arthall.modam.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ShowDto {
    private int performanceId;
    private int showId;
    private LocalDate showDate;
    private int showTime; //회차. 1은 13시, 2는 17시 회차
    private int seatLimit;
    private int seatAvailable;
}
