package com.arthall.modam.service;

import org.springframework.stereotype.Service;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입 처리
    public void registerUser(UserDto userDto) {
        // UserDTO를 UserEntity로 변환
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userDto.getUserId());
        userEntity.setPassword(userDto.getPassword()); // 비밀번호 암호화는 추후 추가
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhoneNumber(userDto.getPhoneNumber());

        // 데이터베이스에 저장
        userRepository.save(userEntity);
    }
}
