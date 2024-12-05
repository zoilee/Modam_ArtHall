package com.arthall.modam.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesDataDto {
    private String title;       // 공연 제목
    private BigDecimal totalSales; // 총 매출
}
