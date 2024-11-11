package com.arthall.modam.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserDto {
    private Integer id;

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영문 소문자와 숫자 4~12자리여야 합니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message = "이름")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email
    private String email;

    private String phoneNumber;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    
}
