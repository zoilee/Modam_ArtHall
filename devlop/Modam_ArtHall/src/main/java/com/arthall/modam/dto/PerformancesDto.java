package com.arthall.modam.dto;

import com.arthall.modam.entity.PerformancesEntity;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
    private String imageUrl; // 이미지 URL
    private double reservationRate; // 예매율
    private int reservationCount; // 예약 수

    // 기존 PerformancesEntity에서 Dto로 변환하는 메서드
    public static PerformancesDto toPerformancesDto(PerformancesEntity performancesEntity) {
        PerformancesDto dto = new PerformancesDto();
        dto.setId(performancesEntity.getId());
        dto.setTitle(performancesEntity.getTitle());
        dto.setDescription(performancesEntity.getDescription());
        dto.setStartDate(performancesEntity.getStartDate()); // 변경된 필드 이름 반영
        dto.setEndDate(performancesEntity.getEndDate());     // 변경된 필드 이름 반영
        dto.setTime(performancesEntity.getTime());
        dto.setLocation(performancesEntity.getLocation());
        dto.setCreatedAt(performancesEntity.getCreatedAt());
        dto.setAge(performancesEntity.getAge());

        return dto;
    }

}
