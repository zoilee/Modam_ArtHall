package com.arthall.modam.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
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

        // 사용자 정보 파싱 (카카오 사용자 정보 가져오기)
        Map<String, Object> attributes = oAuth2User.getAttributes(); // 전체 사용자 정보
        String kakaoId = String.valueOf(attributes.get("id")); // 카카오 고유 ID

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email"); // 이메일
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = (String) properties.get("nickname"); // 닉네임

        // 데이터베이스에 사용자 저장 (또는 기존 사용자 가져오기)
        kakaoUserService.registerKakaoUser(kakaoId, email, nickname);

        // 반환된 OAuth2User는 필요 시 컨트롤러에서 사용 가능
        return oAuth2User;
    }
}