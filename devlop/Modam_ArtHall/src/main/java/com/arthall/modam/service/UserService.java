package com.arthall.modam.service;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.entity.UserRewardEntity;
import com.arthall.modam.repository.UserRepository;
import com.arthall.modam.repository.UserRewardsRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRewardsRepository userRewardsRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                       UserRewardsRepository userRewardsRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRewardsRepository = userRewardsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===============회원가입 처리================
    // 아이디 중복 체크
    public boolean isLoginIdDuplicate(String loginId) {
        return userRepository.findByLoginId(loginId).isPresent();
    }

    // 이메일 중복 체크
    public boolean isEmailDuplicate(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // 전화번호 중복 체크
    public boolean isPhoneNumberDuplicate(String phoneNumber) {
    return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public void registerUser(UserDto userDto) {
        System.out.println("회원가입 요청 처리 중: " + userDto);
    
        // 아이디 중복 체크
        if (isLoginIdDuplicate(userDto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
    
        // 이메일 중복 체크
        if (isEmailDuplicate(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
    
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);
    
        // DTO -> Entity 변환
        UserEntity userEntity = userDto.toEntity();
    
        // 저장
        userRepository.save(userEntity);
    
        System.out.println("회원가입 완료: " + userDto.getLoginId());
    }

    // loginId로 사용자 ID 가져오기
    public int getUserIdByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .map(UserEntity::getId)
                .orElseThrow(() -> {
                    System.err.println("사용자를 찾을 수 없습니다: loginId = " + loginId);
                    return new RuntimeException("사용자를 찾을 수 없습니다: loginId = " + loginId);
                });
    }

    // userId로 사용자 정보 가져오기
    public UserEntity getUserById(int userId) {
        System.out.println("getUserById 호출됨: userId = " + userId);
    
        return userRepository.findById(userId)
            .orElseThrow(() -> {
                System.err.println("사용자를 찾을 수 없습니다: ID = " + userId);
                return new RuntimeException("사용자를 찾을 수 없습니다: ID = " + userId);
            });
    }

    // 추가 메서드 예시: loginId로 사용자 정보 가져오기
    public UserEntity getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: loginId = " + loginId));
    }

    

    // ID 기반 포인트 조회
    public int getUserPointsById(int userId) {
        System.out.println("포인트 조회 중: User ID = " + userId);
        return userRewardsRepository.findByUserId(userId)
                .map(UserRewardEntity::getPoints)
                .orElse(0); // 포인트가 없는 경우 0 반환
    }

    // loginId 기반 포인트 조회
    public int getUserPointsByLoginId(String loginId) {
        UserEntity user = getUserByLoginId(loginId);
        return getUserPointsById(user.getId());
    }

    // UserDetailsService의 메서드 구현 (Spring Security)
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        System.out.println("Spring Security 사용자 인증 중: loginId = " + loginId);

        UserEntity userEntity = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    System.err.println("사용자를 찾을 수 없습니다: loginId = " + loginId);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: loginId = " + loginId);
                });

        return User.builder()
                .username(userEntity.getLoginId())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().name()) // 사용자 역할 설정
                .build();
    }

    // 관리자 계정 자동 생성
    @PostConstruct
    public void createAdminUser() {
        if (userRepository.findByLoginId("admin").isEmpty()) {
            UserEntity adminUser = new UserEntity();
            adminUser.setLoginId("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // 기본 비밀번호
            adminUser.setName("관리자");
            adminUser.setEmail("admin@example.com");
            adminUser.setPhoneNumber("010-1234-5678");
            adminUser.setRole(UserEntity.Role.ADMIN); // 관리자 권한 설정

            userRepository.save(adminUser);
            System.out.println("관리자 계정 생성 완료: admin");
        }
    }

    public boolean login(String loginId, String rawPassword) {
        return userRepository.findByLoginId(loginId)
            .map(user -> passwordEncoder.matches(rawPassword, user.getPassword())) // 비밀번호 비교
            .orElse(false); // 사용자가 존재하지 않을 경우 false 반환
    }

    public boolean isAdmin(UserEntity user) {
        return user.getRole() == UserEntity.Role.ADMIN;
    }
}



