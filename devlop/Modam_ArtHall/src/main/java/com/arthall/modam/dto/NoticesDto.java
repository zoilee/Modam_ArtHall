package com.arthall.modam.dto;

import com.arthall.modam.entity.NoticesEntity;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class NoticesDto {
    private int id; // 공지사항 ID (기본 키)
    private int adminId; // 관리자 ID
    private String title; // 제목
    private String content; // 내용
    private Timestamp createdAt; // 생성 날짜 및 시간

    // NoticesEntity를 NoticeDto로 변환하는 메서드
    public static NoticesDto toNoticeDto(NoticesEntity noticesEntity) {
        NoticesDto dto = new NoticesDto();
        dto.setId(noticesEntity.getId());
        dto.setAdminId(noticesEntity.getAdminId());
        dto.setTitle(noticesEntity.getTitle());
        dto.setContent(noticesEntity.getContent());
        dto.setCreatedAt(noticesEntity.getCreatedAt());

        return dto;
    }
}