package com.arthall.modam.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.UserRepository;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 네이버에서 받은 사용자 정보를 매핑
        Map<String, Object> attributes = oAuth2User.getAttributes();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        String id = (String) response.getOrDefault("id", "default_id");  // id가 없을 경우 기본값 설정
        String name = (String) response.get("name");
        String email = (String) response.get("email");
        String phone = (String) response.get("mobile");
        logger.info("Naver User ID: {}", id);
        // 이메일로 사용자 정보 조회 후 없으면 새로 저장
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPhoneNumber(phone);
            newUser.setRole(UserEntity.Role.USER); // 기본 역할 설정
            newUser.setStatus("active"); // 기본 상태 설정

            userRepository.save(newUser);
        }

        // OAuth2User를 반환하기 위해 필요한 권한 설정 (기본 권한 USER)
        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            response,  // `attributes` 대신 `response`를 전달
            "id"  // user-name-attribute로 설정한 "id"를 기본 식별자로 사용
        );
    }
}