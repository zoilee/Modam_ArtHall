package com.arthall.modam.entity;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Integer id;

    @Column(nullable = false, unique = true) // NOT NULL 추가
    private String loginId; // 로컬 사용자 로그인 ID

    private String password; // 로컬 사용자 비밀번호 (카카오 사용자 NULL 가능)

    @Column(nullable = false)
    private String name; // 사용자 이름 (카카오 닉네임 포함)

    @Column(unique = true, nullable = false)
    private String email; // 이메일

    @Column(name = "phone_number")
    private String phoneNumber; // 휴대폰 번호 (NULL 가능)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER; // 사용자 역할 (USER 또는 ADMIN)

    // Enum for role
    public enum Role {
        USER, ADMIN
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status; // 기본값 설정

    public enum Status {
        ACTIVE,
        BANNED
    }

    @Column(name = "provider", nullable = false)
    private String provider = "LOCAL"; // 로그인 제공자 (LOCAL, KAKAO, NAVER, GOOGLE)

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

}
