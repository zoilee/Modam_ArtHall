package com.arthall.modam.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private UserRewardsRepository userRewardsRepository;
    
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 처리
    public void registerUser(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLoginId(userDto.getLoginId());
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhoneNumber(userDto.getPhoneNumber());
        userEntity.setRole(UserEntity.Role.USER);
        userRepository.save(userEntity);
    }

    public boolean login(String loginId, String rawPassword) {
        Optional<UserEntity> user = userRepository.findByLoginId(loginId);
    
        if (user.isPresent()) {
            System.out.println("사용자 찾음: " + user.get().getLoginId());
            if (passwordEncoder.matches(rawPassword, user.get().getPassword())) {
                System.out.println("비밀번호 일치");
                return true;
            } else {
                System.out.println("비밀번호 불일치");
            }
        } else {
            System.out.println("사용자 없음: " + loginId);
        }
        return false;
    }


    public int getUserPoints(int userId) {
        return userRewardsRepository.findByUserId(userId)
                .map(UserRewardEntity::getPoints)
                .orElse(0);
    }

    // UserDetailsService 인터페이스의 loadUserByUsername 메서드 구현
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        System.out.println("Authenticating user: " + loginId); // 디버깅용 로그 추가

        UserEntity userEntity = userRepository.findByLoginId(loginId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + loginId));
        
        System.out.println("User found: " + userEntity.getLoginId()); // 디버깅용 로그 추가

        return User.builder()
                .username(userEntity.getLoginId())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().name())  // 역할 설정
                .build();
    }

    @PostConstruct
    public void createAdminUser() {
        Optional<UserEntity> admin = userRepository.findByLoginId("admin");
        if (admin.isEmpty()) {
            UserEntity adminUser = new UserEntity();
            adminUser.setLoginId("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // 비밀번호 암호화
            adminUser.setName("관리자");
            adminUser.setEmail("admin@example.com");
            adminUser.setPhoneNumber("010-1234-5678");
            adminUser.setRole(UserEntity.Role.ADMIN); // 관리자 권한 설정
            adminUser.setStatus("active");
            userRepository.save(adminUser);
            System.out.println("관리자 계정 생성 완료");
        }
    }
}
