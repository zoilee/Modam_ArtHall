package com.arthall.modam.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.UserRepository;

@Service
public class KakaoUserService {
private UserRepository userRepository;

public KakaoUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity registerUser(String loginId, String email, String name, String provider) {
        Optional<UserEntity> existingUser = userRepository.findByLoginId(loginId);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        UserEntity user = new UserEntity();
        user.setLoginId(loginId); // 카카오 또는 네이버 ID 저장
        user.setEmail(email);
        user.setName(name);
        user.setProvider(provider); // KAKAO 또는 NAVER
        user.setStatus(UserEntity.Status.ACTIVE);

        return userRepository.save(user);
    }
}
