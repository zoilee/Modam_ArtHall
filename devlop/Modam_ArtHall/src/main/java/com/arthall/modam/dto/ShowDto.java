package com.arthall.modam.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ShowDto {
    private int showId;
    private Date showDate;
    private int showTime;
    private int seatLimit;
    private int seatAvail;
}
