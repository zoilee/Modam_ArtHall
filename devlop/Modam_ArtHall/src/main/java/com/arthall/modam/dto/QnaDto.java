package com.arthall.modam.dto;

import lombok.Data;

@Data
public class QnaDto {
    private Integer userId;
    private String title;
    private String contents;
    private String answer;
}
