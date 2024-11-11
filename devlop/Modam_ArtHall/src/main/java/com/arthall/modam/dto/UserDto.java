package com.arthall.modam.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserDto {

    private Integer id;
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    
}
