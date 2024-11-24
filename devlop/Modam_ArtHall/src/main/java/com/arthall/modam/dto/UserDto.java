package com.arthall.modam.dto;

import java.time.LocalDateTime;

import com.arthall.modam.entity.UserEntity;
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
    public static Object toUserDto(UserEntity userEntity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toUserDto'");
    }
}
