package com.arthall.modam.service;


import java.math.BigDecimal;
import java.sql.Timestamp;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.entity.RewardsEntity;
import com.arthall.modam.repository.UserRepository;
import com.arthall.modam.repository.RewardsRepository;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RewardsRepository RewardsRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                       RewardsRepository RewardsRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.RewardsRepository = RewardsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 처리
    public void registerUser(UserDto userDto) {
        if (userRepository.findByLoginId(userDto.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setLoginId(userDto.getLoginId());
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhoneNumber(userDto.getPhoneNumber());
        userEntity.setRole(UserEntity.Role.USER);

        userRepository.save(userEntity);
        System.out.println("새 사용자 등록: " + userDto.getLoginId());
        // Rewards 생성
        RewardsEntity rewards = new RewardsEntity();
        rewards.setUserId(userEntity.getId()); // 저장된 userEntity의 ID 사용
        rewards.setTotalPoint(BigDecimal.ZERO); // 초기 적립금 0
        rewards.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        RewardsRepository.save(rewards);
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
    public BigDecimal getUserPointsById(int userId) {
        System.out.println("포인트 조회 중: User ID = " + userId);
        return RewardsRepository.findByUserId(userId)
                .map(RewardsEntity::getTotalPoint)
                .orElse(BigDecimal.ZERO); // 포인트가 없는 경우 0 반환
    }

    // loginId 기반 포인트 조회
    public BigDecimal getUserPointsByLoginId(String loginId) {
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

    public UserEntity saveUser(UserEntity user) {
        UserEntity savedUser = userRepository.save(user);

        // 사용자 저장 시 적립금 기본값 설정
        RewardsEntity rewards = new RewardsEntity();
        rewards.setUserId(savedUser.getId());
        rewards.setTotalPoint(BigDecimal.ZERO);
        RewardsRepository.save(rewards);

        return savedUser;
    }
}



