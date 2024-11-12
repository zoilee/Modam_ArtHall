package com.arthall.modam.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ShowDto {
    private int showId;
    private LocalDate showDate;
    private int showTime;
    private int seatLimit;
}
