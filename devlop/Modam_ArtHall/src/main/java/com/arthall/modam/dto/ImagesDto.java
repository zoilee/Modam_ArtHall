package com.arthall.modam.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ImagesDto {
    private int id; // 이미지 ID (기본 키)
    private int referenceId; // 참조 ID (다른 테이블과 연관)
    private String referenceType; // 참조 타입 ('performance', 'notice', 'review')
    private String imageUrl; // 이미지 URL
    private String altText; // 이미지 대체 텍스트
    private Timestamp uploadedAt; // 업로드 시간
}
