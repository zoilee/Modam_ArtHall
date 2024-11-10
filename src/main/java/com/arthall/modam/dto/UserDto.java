package com.arthall.modam.dto;

import lombok.Data;

@Data
public class UserDto {
    private String userId;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
}
