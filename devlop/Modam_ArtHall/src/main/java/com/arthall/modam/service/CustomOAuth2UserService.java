package com.arthall.modam.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final KakaoUserService kakaoUserService;

    public CustomOAuth2UserService(KakaoUserService kakaoUserService) {
        this.kakaoUserService = kakaoUserService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // Spring Security에서 기본적으로 제공하는 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 플랫폼 구분
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String loginId = null;
        String email = null;
        String name = null;

        if ("google".equals(registrationId)) {
            loginId = (String) attributes.get("sub"); // 구글 고유 ID
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            loginId = String.valueOf(attributes.get("id")); // 카카오 고유 ID
            email = (String) kakaoAccount.get("email");
            name = (String) properties.get("nickname");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            loginId = (String) response.get("id"); // 네이버 고유 ID
            email = (String) response.get("email");
            name = (String) response.get("name");
        }

        if (loginId == null) {
            throw new RuntimeException("OAuth2 공급자로부터 loginId를 가져올 수 없습니다.");
        }

        // 사용자 정보 저장 또는 조회
        kakaoUserService.registerUser(loginId, email, name, registrationId.toUpperCase());

        // 새로운 OAuth2User를 반환하며 loginId 포함
        Map<String, Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("loginId", loginId); // loginId를 추가

        // ROLE_USER 권한 추가
        Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), customAttributes, "loginId");

    }
}