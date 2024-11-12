package com.arthall.modam.dto;

import lombok.Data;

@Data
public class MusicalDto {
    private int musicalId;
    private String musicalTitle;
    private int musicalDuration;
    private int musicalAgeLimit;
    private int musicalVIPValue;
    private int musicalRValue;
    private int musicalSValue;
    private int musicalAValue;
}
