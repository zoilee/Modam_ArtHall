package com.arthall.modam.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.arthall.modam.entity.RewardsEntity;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.entity.UserEntity.Status;
import com.arthall.modam.repository.RewardsRepository;
import com.arthall.modam.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final KakaoUserService kakaoUserService;
    private final UserRepository userRepository;
    
    @Autowired
    RewardsRepository rewardsRepository;
   

    public CustomOAuth2UserService(KakaoUserService kakaoUserService, UserRepository userRepository) {
        this.kakaoUserService = kakaoUserService;
        this.userRepository = userRepository;
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
        

        // 데이터베이스에서 사용자 정보 확인
        Optional<UserEntity> existingUserOpt = userRepository.findByLoginId(loginId);


        // 사용자 상태 확인
        if (existingUserOpt.isPresent()) {
            UserEntity existingUser = existingUserOpt.get();

            // 상태가 BANNED인 경우 로그인 차단
            if (existingUser.getStatus() == Status.BANNED) {
                throw new UserService.UserBannedException("계정이 정지되었습니다. 관리자에게 문의하세요.");
            }

            // 기존 사용자 반환
            return createOAuth2User(oAuth2User, attributes, loginId);
        }

        
        
        // 사용자 정보 저장 또는 조회
        kakaoUserService.registerUser(loginId, email, name, registrationId.toUpperCase());

        UserEntity thisUser = userRepository.findByLoginId(loginId).orElseThrow();
        int userId = thisUser.getId();

        // Rewards 생성
        RewardsEntity rewards = new RewardsEntity();
        rewards.setUserId(userId); // 저장된 userEntity의 ID 사용
        rewards.setTotalPoint(BigDecimal.ZERO); // 초기 적립금 0
        rewards.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        rewardsRepository.save(rewards);

        System.out.println("user Rewards 생성 " + rewards);

        // OAuth2User 생성 및 반환
         return createOAuth2User(oAuth2User, attributes, loginId);
        }
        private OAuth2User createOAuth2User(OAuth2User oAuth2User, Map<String, Object> attributes, String loginId) {
            Map<String, Object> customAttributes = new HashMap<>(attributes);
            customAttributes.put("loginId", loginId); // loginId를 추가
            
              // 소셜 로그인 사용자를 ROLE_USER로 설정
    Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

    // DefaultOAuth2User 대신 통합된 사용자 객체 생성
    return new DefaultOAuth2User(authorities, customAttributes, "loginId");
        }
}
