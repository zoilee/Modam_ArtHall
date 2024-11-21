package com.arthall.modam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 로그인 처리
    public boolean login(String loginId, String rawPassword) {
        return userRepository.findByLoginId(loginId)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .isPresent();
    }

    // 포인트 조회
    public int getUserPoints(int userId) {
        return userRewardsRepository.findByUserId(userId)
                .map(UserRewardEntity::getPoints)
                .orElse(0);
    }

    // UserDetailsService 인터페이스의 loadUserByUsername 메서드 구현
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + loginId));

        return User.builder()
                .username(userEntity.getLoginId())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().name()) // 역할 설정
                .build();
    }

    // 관리자 계정 생성 (PostConstruct)
    @PostConstruct
    public void createAdminUser() {
        userRepository.findByLoginId("admin").ifPresentOrElse(admin -> {
            System.out.println("관리자 계정이 이미 존재합니다.");
        }, () -> {
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
        });
    }

/*/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// */

    // 회원 ID로 회원 조회
    public UserEntity getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다. ID: " + userId));
    }

    // 회원 검색
    public Page<UserEntity> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return userRepository.findAll(pageable); // 키워드가 없으면 전체 검색
        } else {
            return userRepository.findByNameContainingIgnoreCase(keyword, pageable); // 키워드로 검색
        }
    }
    
    
    
    // 회원 정보 수정
    public void updateUser(int userId, String loginId, String name, String email, String phoneNumber, String role) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다. ID: " + userId));

        user.setLoginId(loginId);
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setRole(UserEntity.Role.valueOf(role.toUpperCase())); // 역할 설정 (ADMIN, USER 등)
        userRepository.save(user);
    }

    // 회원 삭제
    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("삭제할 회원 정보를 찾을 수 없습니다. ID: " + userId);
        }
        userRepository.deleteById(userId);
    }
}
