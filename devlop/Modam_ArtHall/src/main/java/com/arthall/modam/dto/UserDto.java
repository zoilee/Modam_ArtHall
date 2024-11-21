package com.arthall.modam.dto;

import java.time.LocalDateTime;

import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.entity.UserEntity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    private Integer id;

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다.")
    @Pattern(regexp = "^[A-Za-z0-9]{4,20}$", message = "아이디는 영문 대소문자와 숫자만 입력 가능합니다.")
    private String loginId;
    
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    @Pattern(regexp = "^[A-Za-z\\d]{8,20}$", message = "비밀번호는 8~20자, 영문 대소문자 또는 숫자로만 입력 가능합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(max = 50, message = "이름은 최대 50자까지 입력 가능합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z]+$", message = "이름은 한글과 영문만 입력 가능합니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 주소를 입력하세요.")
    @Size(max = 255, message = "이메일은 최대 255자까지 입력 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}$", message = "올바른 이메일 형식을 입력하세요. 예: example@domain.com")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자로 입력해주세요.")
    private String phoneNumber;

    private Role role; // UserEntity.Role로 변경
    private String status;
    private LocalDateTime createdAt;

        public UserEntity toEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setLoginId(this.loginId);
        userEntity.setPassword(this.password); // 암호화된 비밀번호
        userEntity.setName(this.name);
        userEntity.setEmail(this.email);
        userEntity.setPhoneNumber(this.phoneNumber);
        userEntity.setRole(UserEntity.Role.USER); // 기본 역할 설정
        return userEntity;
    }
}
