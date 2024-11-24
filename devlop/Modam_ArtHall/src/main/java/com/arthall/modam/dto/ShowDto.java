package com.arthall.modam.dto;

import java.sql.Date;

import com.arthall.modam.entity.ShowEntity;

import lombok.Data;

@Data
public class ShowDto {
    private int showId;
    private Date showDate;
    private int showTime; //회차. 1은 13시, 2는 17시 회차
    private int seatLimit;
    private int seatAvailable;
    private int performanceId;
    public static Object toShowDto(ShowEntity showEntity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toShowDto'");
    }
}
