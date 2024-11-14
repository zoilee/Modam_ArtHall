package com.arthall.modam.dto;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class PerformanceDto {
    private int id;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private int time;
    private int age;
    private String location;
    private Timestamp created_at;
}
