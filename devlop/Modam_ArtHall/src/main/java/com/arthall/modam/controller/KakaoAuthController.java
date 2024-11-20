package com.arthall.modam.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoAuthController {
private final ClientRegistrationRepository clientRegistrationRepository;

    public KakaoAuthController(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    // 카카오 로그인 페이지로 리다이렉트
    @GetMapping("/oauth2/code/kakao")
    public String loginKakao(Model model, OAuth2AuthenticationToken authenticationToken) {
        // 인증된 사용자 정보 가져오기
        OidcUser kakaoUser = (OidcUser) authenticationToken.getPrincipal();

        // 사용자 정보 추출
        String kakaoId = kakaoUser.getName();
        String email = kakaoUser.getAttribute("email");
        String nickname = kakaoUser.getAttribute("nickname");

        // 사용자 정보를 모델에 추가하여 화면에서 사용
        model.addAttribute("kakaoId", kakaoId);
        model.addAttribute("email", email);
        model.addAttribute("nickname", nickname);

        return "main"; // 인증 후 리다이렉트될 대시보드 페이지
    }
}
