package com.arthall.modam.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 처리
    public void registerUser(UserDto userDto) {
        // UserDto를 UserEntity로 변환
        UserEntity userEntity = new UserEntity();
        userEntity.setLoginId(userDto.getLoginId());
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhoneNumber(userDto.getPhoneNumber());

        // 데이터베이스에 저장
        userRepository.save(userEntity);
    }

    // 로그인
    public boolean login(String loginId, String rawPassword){
        UserEntity user = userRepository.findByLoginId(loginId);

        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            // 로그인 성공
            return true;
        }
        // 로그인 실패
        return false;
    }
}
