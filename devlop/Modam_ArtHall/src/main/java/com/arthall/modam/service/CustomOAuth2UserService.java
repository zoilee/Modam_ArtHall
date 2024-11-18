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

        // OAuth2 provider (Google, Kakao, Naver 등) 식별
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = null;
        String name = null;

        if ("kakao".equals(registrationId)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            @SuppressWarnings("unchecked")
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
            logger.info("Kakao User - Email: {}, Name: {}", email, name);
        } else if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            logger.info("Google User - Email: {}, Name: {}", email, name);
        } else if ("naver".equals(registrationId)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");

            email = (String) response.get("email");
            name = (String) response.get("name");
            logger.info("Naver User - Email: {}, Name: {}", email, name);
        }

        if (email == null) {
            throw new IllegalArgumentException("Email is missing from the OAuth2 provider");
        }

        // 사용자 정보 저장 또는 업데이트
        saveOrUpdateUser(email, name);

        // OAuth2User 반환
        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "email"  // OAuth2 사용자 식별자로 사용할 속성 (공통적으로 email 사용)
        );
    }

    private void saveOrUpdateUser(String email, String name) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRole(UserEntity.Role.USER); // 기본 사용자 역할 설정
            newUser.setStatus("active"); // 기본 상태 설정
            userRepository.save(newUser);
            logger.info("New user registered: {}", email);
        } else {
            UserEntity existing = existingUser.get();
            existing.setName(name); // 이름 업데이트 (필요 시)
            userRepository.save(existing);
            logger.info("Existing user updated: {}", email);
        }
    }
}
