package com.arthall.modam.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class QnaDto {
    private Integer userId;
    private String title;
    private String contents;
    private String answer;
    private Timestamp createdAt; // TIMESTAMP 데이터
}
