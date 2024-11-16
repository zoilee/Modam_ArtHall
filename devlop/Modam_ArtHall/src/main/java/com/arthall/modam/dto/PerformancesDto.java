package com.arthall.modam.dto;

import com.arthall.modam.entity.PerformancesEntity;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class PerformancesDto {
    private int id; // 공연 ID (기본 키)
    private String title; // 제목
    private String description; // 설명
    private Date startDate; // 시작 날짜
    private Date endDate; // 종료 날짜
    private int time; // 공연 시간
    private String location; // 장소
    private Timestamp createdAt; // 생성 날짜 및 시간
    private int age; // 관람 연령

    public static PerformancesDto toPerformancesDto(PerformancesEntity performancesEntity) {
        PerformancesDto dto = new PerformancesDto();
        dto.setId(performancesEntity.getId());
        dto.setTitle(performancesEntity.getTitle());
        dto.setDescription(performancesEntity.getDescription());
        dto.setStartDate(performancesEntity.getStart_date());
        dto.setEndDate(performancesEntity.getEnd_date());
        dto.setTime(performancesEntity.getTime());
        dto.setLocation(performancesEntity.getLocation());
        dto.setCreatedAt(performancesEntity.getCreatedAt());

        return dto;
    }

}
