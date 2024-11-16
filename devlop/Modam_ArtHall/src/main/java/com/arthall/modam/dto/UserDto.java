package com.arthall.modam.dto;

import java.time.LocalDateTime;

import com.arthall.modam.entity.UserEntity.Role;

import lombok.Data;

@Data
public class UserDto {
    private Integer id;
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role; // UserEntity.Role로 변경
    private String status;
    private LocalDateTime createdAt;
}
